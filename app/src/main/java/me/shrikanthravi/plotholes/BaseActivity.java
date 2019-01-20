package me.shrikanthravi.plotholes;

import android.animation.LayoutTransition;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.shrikanthravi.plotholes.adapters.AutofillAdapter;
import me.shrikanthravi.plotholes.api.models.general.AutofillRes;
import me.shrikanthravi.plotholes.api.services.ApiClient;
import me.shrikanthravi.plotholes.api.services.ApiInterface;
import me.shrikanthravi.plotholes.api.services.AzureDB;
import me.shrikanthravi.plotholes.extras.TinyDB;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity implements AutofillAdapter.MyCallback {

    public MapboxMap mapboxMap1;

    @BindView(R.id.where)
    EditText whereto;

    @BindView(R.id.calibrateBTN)
    Button calibrateBTN;

    @BindView(R.id.autofill)
    RecyclerView autofill_recycler;

    @BindView(R.id.mapView)
    MapView mapView;

    Bitmap potholeIcon;



    public static float calib_X, calib_Y, calib_Z;
    float x, y, z;
    SensorBroadcastReceiver sensorBroadcastReceiver;
    private TinyDB db;
    private ApiInterface api;
    //private ApiInterface api1;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private ArrayList<String> placename = new ArrayList<>();
    private ArrayList<String> placeaddress = new ArrayList<>();
    private ArrayList<Double> latitude = new ArrayList<>();
    private ArrayList<Double> longitude = new ArrayList<>();

    public abstract void onAppMapReady(MapboxMap mapboxMap);

    public abstract void onAutofillRowSelected(double latitude, double longitude);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);



        /*ViewGroup layout = (ViewGroup) findViewById(R.id.topLL);
        LayoutTransition layoutTransition = layout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);*/

        db = new TinyDB(getApplicationContext());
        api = ApiClient.getClient().create(ApiInterface.class);
        //api1 = AzureDB.getClient().create(ApiInterface.class);
        ButterKnife.bind(this);
        sensorBroadcastReceiver = new SensorBroadcastReceiver();
        final IntentFilter intentFilter = new IntentFilter("SensorBroadcastReceiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(sensorBroadcastReceiver, intentFilter);
        initMongoDb();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                mapboxMap1 = mapboxMap;
                mapboxMap.setMinZoomPreference(2.5);
                mapboxMap.setMaxZoomPreference(18.5);
                mapboxMap.getUiSettings().setAttributionEnabled(false);
                mapboxMap.getUiSettings().setRotateGesturesEnabled(false);
                mapboxMap.getUiSettings().setTiltGesturesEnabled(false);
                mapboxMap.setPadding(20, 20, 20, 20);
                mapboxMap.getUiSettings().setLogoMargins(0, 0, 0, 0);
               
                onAppMapReady(mapboxMap);
            }
        });



        whereto.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (whereto.getText().toString().length() > 1) {
                    placename.clear();
                    placeaddress.clear();
                    latitude.clear();
                    longitude.clear();
                    //autofill_recycler.setVisibility(View.VISIBLE);
                    api.autfillPlaces(db.getString("access_token"), whereto.getText().toString()).enqueue(new Callback<AutofillRes>() {
                        @Override
                        public void onResponse(Call<AutofillRes> call, Response<AutofillRes> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    for (int i = 0; i < response.body().suggestedLocationsRes().size(); i++) {
                                        placename.add(response.body().suggestedLocationsRes().get(i).getPlaceName());
                                        placeaddress.add(response.body().suggestedLocationsRes().get(i).getPlaceAddress());
                                        latitude.add(response.body().suggestedLocationsRes().get(i).getLatitude());
                                        longitude.add(response.body().suggestedLocationsRes().get(i).getLongitude());
                                    }
                                    if (adapter == null) {
                                        adapter = new AutofillAdapter(BaseActivity.this, placename, placeaddress, BaseActivity.this);
                                        autofill_recycler.setLayoutManager(new LinearLayoutManager(BaseActivity.this, LinearLayoutManager.VERTICAL, false));
                                        autofill_recycler.setAdapter(adapter);
                                    } else {
                                        adapter.notifyDataSetChanged();
                                        autofill_recycler.scrollToPosition(0);
                                        autofill_recycler.setVisibility(View.VISIBLE);
                                    }
                                }
                            } else {
                                Toast.makeText(BaseActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AutofillRes> call, Throwable t) {
                            Toast.makeText(BaseActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                if (s.toString().trim().length()==0){
                    placename.clear();
                    placeaddress.clear();
                    latitude.clear();
                    longitude.clear();
                    if(adapter!=null) {
                        adapter.notifyDataSetChanged();
                    }
                    autofill_recycler.setVisibility(View.GONE);
                }
            }
        });

        calibrateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calib_X = x;
                calib_Y = y;
                calib_Z = z;
            }
        });
    }

    private void initMongoDb() {

        //MongoClient mongoClient = new MongoClient("mongodb://plothole:bvj17qKni6FWqLXtJzfKftwpLyQJ0Wf1pjlz66LPlzDAHgXerdvEsUHo3rCMiBFRJDCV56JYCjOLdHrxb6Dfgw==@plothole.documents.azure.com:10255/?ssl=true&replicaSet=globaldb");
        //DB database = mongoClient.getDB("plothole_locs");

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public class SensorBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle b = intent.getExtras();
            x = b.getFloat("Acc_X");
            y = b.getFloat("Acc_Y");
            z = b.getFloat("Acc_Z");

            String actX = Float.toString(round(x, 3));
            String actY = Float.toString(round(y, 3));
            String actZ = Float.toString(round(z, 3));

            String calibX = Float.toString(round((x - calib_X), 3));
            String calibY = Float.toString(round((y - calib_Y), 3));
            String calibZ = Float.toString(round((z - calib_Z), 3));

            String result = "ACT X -> " + actX + "\nACT Y -> " + actY + "\nACT Z -> " + actZ;
            result = result + "\nCalib X -> " + calibX + "\nCalib Y -> " + calibY + "\nCalib Z -> " + calibZ;
        }
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    @Override
    public void onTouched(int position) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        autofill_recycler.setVisibility(View.GONE);
        onAutofillRowSelected(latitude.get(position), longitude.get(position));
    }

    public void plotPotholes(MapboxMap mapboxMap){

        System.out.println("shrikanth pothole plot testing -> "+HomeActivity.potholeLocations.size());

        for(int i=0;i<HomeActivity.potholeLocations.size();i++){

            LatLng latLng = new LatLng();
            latLng.setLatitude(Double.valueOf(HomeActivity.potholeLocations.get(i).getLatitude()));
            latLng.setLongitude(Double.valueOf(HomeActivity.potholeLocations.get(i).getLongitude()));
            MarkerOptions  markerOptions = new MarkerOptions().position(latLng);
            //Marker marker1 = map.addMarker(markerOptions);
            markerOptions.setTitle("");
            markerOptions.setSnippet("");
            mapboxMap.addMarker(markerOptions);

        }

    }
}