package me.shrikanthravi.plotholes.extras;

import android.app.Application;
import com.mapbox.mapboxsdk.MapmyIndia;
import com.mmi.services.account.MapmyIndiaAccountManager;

public class SetupMapMyIndia extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MapmyIndiaAccountManager.getInstance().setRestAPIKey(Config.rest_api_key);
        MapmyIndiaAccountManager.getInstance().setMapSDKKey(Config.map_sdk_key);
        MapmyIndiaAccountManager.getInstance().setAtlasClientId(Config.client_id);
        MapmyIndiaAccountManager.getInstance().setAtlasClientSecret(Config.client_secret);
        MapmyIndiaAccountManager.getInstance().setAtlasGrantType(Config.grant_type);
        MapmyIndiaAccountManager.getInstance().setAtlasAPIVersion(Config.api_version);
        MapmyIndia.getInstance(this);
    }
}