package me.shrikanthravi.plotholes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public abstract class BaseActivity extends AppCompatActivity {

    public MapboxMap mapboxMap;
    MapView mapView;

    public abstract void onAppMapReady(MapboxMap mapboxMap);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
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
}