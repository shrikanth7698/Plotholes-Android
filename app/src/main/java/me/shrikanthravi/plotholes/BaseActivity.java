package me.shrikanthravi.plotholes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import java.math.BigDecimal;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    public MapboxMap mapboxMap;
    MapView mapView;

    @BindView(R.id.xyzTV)
    TextView xyzTV;
    @BindView(R.id.calibrateBTN)
    Button calibrateBTN;
    public static float calib_X,calib_Y,calib_Z;
    float x,y,z;
    SensorBroadcastReceiver sensorBroadcastReceiver;
    public abstract void onAppMapReady(MapboxMap mapboxMap);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        sensorBroadcastReceiver = new SensorBroadcastReceiver();
        final IntentFilter intentFilter = new IntentFilter("SensorBroadcastReceiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(sensorBroadcastReceiver, intentFilter);

        mapView = findViewById(R.id.mapView);
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

            String calibX = Float.toString(round((x-calib_X), 3));
            String calibY = Float.toString(round((y-calib_Y), 3));
            String calibZ = Float.toString(round((z-calib_Z), 3));

            String result = "ACT X -> "+actX+"\nACT Y -> "+actY+"\nACT Z -> "+actZ;
            result = result+"\nCalib X -> "+calibX+"\nCalib Y -> "+calibY+"\nCalib Z -> "+calibZ;

            xyzTV.setText(result);
        }
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}