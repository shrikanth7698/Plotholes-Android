package me.shrikanthravi.plotholes.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SensorService extends Service implements SensorEventListener {

    public SensorService() {
    }
    private static final int N_SAMPLES = 7;
    private static List<Float> x = new ArrayList<>();
    private static List<Float> y = new ArrayList<>();
    private static List<Float> z = new ArrayList<>();
    private TensorFlowClassifier classifier;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    double lat,lng;
    LatLngBroadcastReceiver latLngBroadcastReceiver;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        classifier = new TensorFlowClassifier(getApplicationContext());
        latLngBroadcastReceiver = new LatLngBroadcastReceiver();
        final IntentFilter intentFilter = new IntentFilter("LatLngBroadcastReceiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(latLngBroadcastReceiver, intentFilter);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        final int type = event.sensor.getType();
        classifyData();
        x.add(event.values[0]);
        y.add(event.values[1]);
        z.add(event.values[2]);




        //sendBroadcast(x,y,z);
        //System.out.println("Accelerometer data\nx -> "+x+"\ny -> "+y+"\nz -> "+z);

    }

    private float[] results;

    private void classifyData(){
        if (x.size() == N_SAMPLES && y.size() == N_SAMPLES && z.size() == N_SAMPLES) {

            List<Float> data = new ArrayList<>();
            //data.addAll(slope(x));
            //data.addAll(slope(y));
            //data.addAll(slope(z));
            data.addAll(x);
            data.addAll(y);
            data.addAll(z);
            results = classifier.predictProbabilities(toFloatArray(data));

            String goodProb =  Float.toString(round(results[0], 3));
            String badProb =  Float.toString(round(results[1], 3));
            String resultText = "Good -> "+goodProb+"\nBad -> "+badProb;
            Toast.makeText(getApplicationContext(),resultText,Toast.LENGTH_SHORT).show();

            x.clear();
            y.clear();
            z.clear();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    /*public void sendBroadcast(float x,float y,float z){
        Intent intent = new Intent("SensorBroadcastReceiver");
        Bundle bundle = new Bundle();
        bundle.putFloat("Acc_X",x);
        bundle.putFloat("Acc_Y",y);
        bundle.putFloat("Acc_Z",z);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }*/

    public class LatLngBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle b = intent.getExtras();
            lat = b.getFloat("lat");
            lng = b.getFloat("lng");

        }
    }

    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    List<Float> slope(List<Float> temp){
        System.out.println("shrikanth slope testing --> before"+temp);
        List<Float> slope = new ArrayList<>();
        for(int i=1;i<temp.size();i++){
            slope.add(temp.get(i)-temp.get(i-1));

        }
        System.out.println("shrikanth slope testing --> after"+slope);
        return slope;
    }


}
