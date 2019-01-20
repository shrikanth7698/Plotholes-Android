package me.shrikanthravi.plotholes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerOptions;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;

import java.util.ArrayList;
import java.util.List;

import me.shrikanthravi.plotholes.api.services.AzureDB;
import me.shrikanthravi.plotholes.api.services.ApiInterface;
import me.shrikanthravi.plotholes.data.models.PotholeLocation;
import me.shrikanthravi.plotholes.extras.TinyDB;
import me.shrikanthravi.plotholes.services.SensorService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity implements LocationEngineListener {
    ApiInterface api;
    private LocationLayerPlugin locationLayerPlugin;
    private LocationEngine locationEngine;
    MapboxMap mapboxMap;
    TinyDB tinyDB;
    List<PotholeLocation> potholeLocations = new ArrayList<>();

    @Override
    public void onAppMapReady(final MapboxMap mapboxMap) {
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

    public void check() {
        Log.wtf("DOES IT WORK?", "FUCK YEAH");
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
        tinyDB.putString("lat",String.valueOf(location.getLatitude()));
        tinyDB.putString("lng",String.valueOf(location.getLongitude()));
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
                System.out.println("potholes list size -> "+potholeLocations.size());
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
