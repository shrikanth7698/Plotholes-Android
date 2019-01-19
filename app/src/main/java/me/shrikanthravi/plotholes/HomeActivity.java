package me.shrikanthravi.plotholes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapbox.mapboxsdk.MapmyIndia;
import com.mmi.services.account.MapmyIndiaAccountManager;

import butterknife.ButterKnife;
import me.shrikanthravi.plotholes.services.SensorService;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    public void init(){

        ButterKnife.bind(this);
        MapmyIndiaAccountManager.getInstance().setRestAPIKey("bvyi1llc42hbcyx1u59wmzi4x2acxqtl");
        MapmyIndiaAccountManager.getInstance().setMapSDKKey("lr3xon9422rdzoputa3fmwuja4787z5r");
        MapmyIndiaAccountManager.getInstance().setAtlasGrantType("client_credentials");
        MapmyIndiaAccountManager.getInstance().setAtlasClientId("-T9_3PSz5MZyDVT9A6zVT1Ym8iUtEzQy2Lndg_7SvFM=");
        MapmyIndiaAccountManager.getInstance().setAtlasClientSecret("hk0REzhbnCc6sikl9yGPSCxry6Vydi8Us9JASA2Udcy_wc3uk_b2qA==");
        MapmyIndiaAccountManager.getInstance().setAtlasAPIVersion("1.3.11");
        MapmyIndia.getInstance(getApplicationContext());
        setContentView(R.layout.activity_home);

        //Start this service when in navigation mode
        startService(new Intent(getApplicationContext(), SensorService.class));
    }
}
