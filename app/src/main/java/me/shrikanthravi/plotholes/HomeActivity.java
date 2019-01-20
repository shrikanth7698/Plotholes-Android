package me.shrikanthravi.plotholes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerOptions;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;

import java.util.ArrayList;
import java.util.List;

import me.shrikanthravi.plotholes.api.models.general.RoutingRes;
import me.shrikanthravi.plotholes.api.services.AzureDB;
import me.shrikanthravi.plotholes.api.services.ApiInterface;
import me.shrikanthravi.plotholes.api.services.RoutingClient;
import me.shrikanthravi.plotholes.api.services.RoutingInteface;
import me.shrikanthravi.plotholes.data.models.PotholeLocation;
import me.shrikanthravi.plotholes.extras.Config;
import me.shrikanthravi.plotholes.extras.TinyDB;
import me.shrikanthravi.plotholes.services.SensorService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity implements LocationEngineListener {
    RoutingInteface routeapi;
    ApiInterface api;
    private LocationLayerPlugin locationLayerPlugin;
    private LocationEngine locationEngine;
    MapboxMap mapboxMap;
    TinyDB tinyDB;
    List<PotholeLocation> potholeLocations = new ArrayList<>();
    ArrayList<LatLng> listOfLatlang = new ArrayList<>();

    @Override
    public void onAppMapReady(final MapboxMap mapboxMap) {
        routeapi = RoutingClient.getClient().create(RoutingInteface.class);
        this.mapboxMap = mapboxMap;
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.setFastestInterval(1000);
        locationEngine.addLocationEngineListener(this);
        locationEngine.activate();
        int[] padding;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            padding = new int[]{0, 750, 0, 0};
        } else {
            padding = new int[]{0, 250, 0, 0};
        }
        LocationLayerOptions options = LocationLayerOptions.builder(this)
                .padding(padding)
                .build();
        locationLayerPlugin = new LocationLayerPlugin(mapView, mapboxMap, locationEngine, options);
        getLifecycle().addObserver(locationLayerPlugin);
        startService(new Intent(getApplicationContext(), SensorService.class));
    }

    @Override
    public void onAutofillRowSelected(double latitude, double longitude) {
        routeapi.getRouteWaypoints(Config.rest_api_key, tinyDB.getString("lat") + "," + tinyDB.getString("lng"), latitude + "," + longitude, "true", "1").enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    for (int i = 0; i < response.body().getAsJsonObject().get("results").getAsJsonObject().get("trips").getAsJsonArray().get(0).getAsJsonObject().get("advices").getAsJsonArray().size(); i++) {
                        listOfLatlang.add(new LatLng(response.body().getAsJsonObject().get("results").getAsJsonObject().get("trips").getAsJsonArray().get(0).getAsJsonObject().get("advices").getAsJsonArray().get(i).getAsJsonObject().get("pt").getAsJsonObject().get("lat").getAsDouble(), response.body().getAsJsonObject().get("results").getAsJsonObject().get("trips").getAsJsonArray().get(0).getAsJsonObject().get("advices").getAsJsonArray().get(i).getAsJsonObject().get("pt").getAsJsonObject().get("lng").getAsDouble()));
                    }
                    mapboxMap.addPolyline(new PolylineOptions().addAll(listOfLatlang).color(Color.parseColor("#000000")).width(4));
                    LatLngBounds latLngBounds = new LatLngBounds.Builder().includes(listOfLatlang).build();
                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 70));
                } else {
                    Toast.makeText(HomeActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(HomeActivity.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConnected() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (listOfLatlang.size() == 0) {
            listOfLatlang.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        tinyDB.putString("lat", String.valueOf(location.getLatitude()));
        tinyDB.putString("lng", String.valueOf(location.getLongitude()));
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
        locationEngine.removeLocationEngineListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationEngine.requestLocationUpdates();
            locationEngine.addLocationEngineListener(this);
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
        tinyDB = new TinyDB(getApplicationContext());
        api = AzureDB.getClient().create(ApiInterface.class);
        api.getPotholes().enqueue(new Callback<List<PotholeLocation>>() {
            @Override
            public void onResponse(Call<List<PotholeLocation>> call, Response<List<PotholeLocation>> response) {
                potholeLocations.clear();
                potholeLocations.addAll(response.body());
                System.out.println("potholes list size -> " + potholeLocations.size());
            }

            @Override
            public void onFailure(Call<List<PotholeLocation>> call, Throwable t) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationEngineListener(this);
            locationEngine.removeLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }
}
