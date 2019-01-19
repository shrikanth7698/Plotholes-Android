package me.shrikanthravi.plotholes;

import android.app.Application;
import com.mapbox.mapboxsdk.MapmyIndia;
import com.mmi.services.account.MapmyIndiaAccountManager;

public class SetupMapMyIndia extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MapmyIndiaAccountManager.getInstance().setRestAPIKey("bvyi1llc42hbcyx1u59wmzi4x2acxqtl");
        MapmyIndiaAccountManager.getInstance().setMapSDKKey("lr3xon9422rdzoputa3fmwuja4787z5r");
        MapmyIndiaAccountManager.getInstance().setAtlasClientId("-T9_3PSz5MZyDVT9A6zVT1Ym8iUtEzQy2Lndg_7SvFM=");
        MapmyIndiaAccountManager.getInstance().setAtlasClientSecret("hk0REzhbnCc6sikl9yGPSCxry6Vydi8Us9JASA2Udcy_wc3uk_b2qA==");
        MapmyIndiaAccountManager.getInstance().setAtlasGrantType("client_credentials");
        MapmyIndiaAccountManager.getInstance().setAtlasAPIVersion("1.3.11");
        MapmyIndia.getInstance(this);
    }
}