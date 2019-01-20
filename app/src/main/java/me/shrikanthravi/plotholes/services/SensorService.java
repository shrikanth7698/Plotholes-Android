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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import me.shrikanthravi.plotholes.HomeActivity;
import me.shrikanthravi.plotholes.api.services.AzureDB;
import me.shrikanthravi.plotholes.api.services.ApiInterface;
import me.shrikanthravi.plotholes.extras.TinyDB;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensorService extends Service implements SensorEventListener {

    public SensorService() {
    }
    private ApiInterface api;
    TinyDB tinyDB;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private static final int N_SAMPLES = 16;
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
        api = AzureDB.getClient().create(ApiInterface.class);
        tinyDB = new TinyDB(getApplicationContext());
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        final int type = event.sensor.getType();
        classifyData(tinyDB.getString("lat"),tinyDB.getString("lng"));
        x.add(event.values[0]-HomeActivity.calib_X);
        y.add(event.values[1]-HomeActivity.calib_Y);
        z.add(event.values[2]-HomeActivity.calib_Z);

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        //String output = "X -> "+String.valueOf(x)+"\nY -> "+String.valueOf(y)+"\nZ -> "+String.valueOf(z);

        //Toast.makeText(getApplicationContext(), output,Toast.LENGTH_SHORT).show();




        sendBroadcast(x,y,z);
        //System.out.println("Accelerometer data\nx -> "+x+"\ny -> "+y+"\nz -> "+z);

    }

    public void sendBroadcast(float x,float y,float z){
        Intent intent = new Intent("SensorBroadcastReceiver");
        Bundle bundle = new Bundle();
        bundle.putFloat("Acc_X",x);
        bundle.putFloat("Acc_Y",y);
        bundle.putFloat("Acc_Z",z);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private float[] results;

    private void classifyData(String lat,String lng){
        if (x.size() == N_SAMPLES && y.size() == N_SAMPLES && z.size() == N_SAMPLES) {

            List<Float> data = new ArrayList<>();
            data.addAll(slope(x));
            data.addAll(slope(y));
            data.addAll(slope(z));
            //data.addAll(x);
            //data.addAll(y);
            //data.addAll(z);
            //data.addAll(meanX(x));
            //data.addAll(meanY(y));
            //data.addAll(meanZ(z));
            results = classifier.predictProbabilities(toFloatArray(data));

            String goodProb =  Float.toString(round(results[0], 3));
            String badProb =  Float.toString(round(results[1], 3));
            String resultText = "Good -> "+goodProb+"\nBad -> "+badProb;
            if(round(results[1],3)>=0.9){
                Toast.makeText(getApplicationContext(),"Pothole -> "+badProb,Toast.LENGTH_SHORT).show();
                //TODO upload pothole data to db
                //uploadPotholetoFBDB(lat,lng);
                uploadPotholeDataMongo(lat,lng);

            }

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
        //System.out.println("shrikanth slope testing --> before"+temp);
        List<Float> slope = new ArrayList<>();
        for(int i=1;i<temp.size();i++){
            slope.add(temp.get(i)-temp.get(i-1));

        }
        //System.out.println("shrikanth slope testing --> after"+slope);
        return slope;
    }

    List<Float> meanX(List<Float> temp){
        List<Float> mean = new ArrayList<>();
        for(int i=0;i<temp.size();i++){
            mean.add((float) ((temp.get(i)-1.10155749)/(3.49168468)));
        }
        System.out.println("shrikanth mean testing --> after"+mean);
        return mean;
    }

    List<Float> meanY(List<Float> temp){
        List<Float> mean = new ArrayList<>();
        for(int i=0;i<temp.size();i++){
            mean.add((float) ((temp.get(i)-1.44701922)/(2.45126748)));
        }
        return mean;
    }

    List<Float> meanZ(List<Float> temp){
        List<Float> mean = new ArrayList<>();
        for(int i=0;i<temp.size();i++){
            mean.add((float) ((temp.get(i)-7.38596535)/(5.11600208)));
        }
        return mean;
    }

    public void uploadPotholeDataMongo(String lat,String lng){
        api.uploadPlotHole(lat,lng)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if(response.isSuccessful()){
                            //Toast.makeText(getApplicationContext(),"Uploading pothole data failed",Toast.LENGTH_SHORT).show();
                            //SUCCESS
                        }else {
                            Toast.makeText(getApplicationContext(),"Uploading pothole data failed",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    public void uploadPotholetoFBDB(String lat,String lng){
        String key = myRef.child("potholes").push().getKey();
        myRef.child("potholes").child(key).child("lat").setValue(lat);
        myRef.child("potholes").child(key).child("lng").setValue(lng);

    }

}
