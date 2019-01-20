package me.shrikanthravi.plotholes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.shrikanthravi.plotholes.api.models.general.AutofillRes;
import me.shrikanthravi.plotholes.api.services.ApiClient;
import me.shrikanthravi.plotholes.api.services.ApiInterface;
import me.shrikanthravi.plotholes.extras.TinyDB;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity {

    public MapboxMap mapboxMap;

    @BindView(R.id.whereto)
    EditText whereto;

    @BindView(R.id.calibrateBTN)
    Button calibrateBTN;

    @BindView(R.id.autofill)
    RecyclerView autofill_recycler;

    @BindView(R.id.mapView)
    MapView mapView;

    public static float calib_X, calib_Y, calib_Z;
    float x, y, z;
    SensorBroadcastReceiver sensorBroadcastReceiver;
    private TinyDB db;
    private ApiInterface api;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private ArrayList<String> placename = new ArrayList<>();
    private ArrayList<String> placeaddress = new ArrayList<>();
    private ArrayList<String> latitude = new ArrayList<>();
    private ArrayList<String> longitude = new ArrayList<>();

    public abstract void onAppMapReady(MapboxMap mapboxMap);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        db = new TinyDB(getApplicationContext());
        api = ApiClient.getClient().create(ApiInterface.class);
        ButterKnife.bind(this);
        sensorBroadcastReceiver = new SensorBroadcastReceiver();
        final IntentFilter intentFilter = new IntentFilter("SensorBroadcastReceiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(sensorBroadcastReceiver, intentFilter);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
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

                    api.autfillPlaces(db.getString("access_token"), whereto.getText().toString()).enqueue(new Callback<AutofillRes>() {
                        @Override
                        public void onResponse(Call<AutofillRes> call, Response<AutofillRes> response) {
                            if (response.isSuccessful()) {
                                if (response != null) {
                                    for (int i=0; i<response.body().suggestedLocationsRes().size(); i++) {
                                        placename.add(response.body().suggestedLocationsRes().get(i).getPlaceName());
                                        placeaddress.add(response.body().suggestedLocationsRes().get(i).getPlaceAddress());
                                        latitude.add(response.body().suggestedLocationsRes().get(i).getLatitude());
                                        placeaddress.add(response.body().suggestedLocationsRes().get(i).getPlaceAddress());
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
}