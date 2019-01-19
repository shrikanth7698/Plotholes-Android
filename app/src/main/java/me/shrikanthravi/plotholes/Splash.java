package me.shrikanthravi.plotholes;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import me.shrikanthravi.plotholes.api.models.oauth.OAuthRes;
import me.shrikanthravi.plotholes.api.services.OAuthClient;
import me.shrikanthravi.plotholes.api.services.OAuthInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class Splash extends AppCompatActivity {
    String[] PERMISSIONS_REQUIRED = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE};
    private OAuthInterface oAuthInterface;
    private TinyDB db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        db = new TinyDB(getApplicationContext());
        oAuthInterface = OAuthClient.getClient().create(OAuthInterface.class);
        oAuthInterface.getAccessToken(Config.grant_type, Config.client_id, Config.client_secret).enqueue(new Callback<OAuthRes>() {
            @Override
            public void onResponse(Call<OAuthRes> call, Response<OAuthRes> response) {
                if (response.isSuccessful()) {
                    db.putString("access_token", response.body().getTokenType() + " " + response.body().getAccessToken());
                    requestPermissions();
                }
                else {
                    Toast.makeText(Splash.this, response.toString(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<OAuthRes> call, Throwable t) {
                Toast.makeText(Splash.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionGranted = true;
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PERMISSION_GRANTED) {
                    permissionGranted = false;
                    break;
                }
            }
        } else {
            permissionGranted = false;
        }

        if (!permissionGranted) {
            new AlertDialog.Builder(this).setTitle("Please grant all the permissions to continue. \nYou can go to phone's settings >> Applications >> Orrder Driver and manually grant the permissions.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions();
                        }
                    }).show();
        } else {
            redirect();
        }
    }

    void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, PERMISSIONS_REQUIRED[0]) == PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, PERMISSIONS_REQUIRED[1]) == PERMISSION_GRANTED) {
            redirect();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSIONS_REQUIRED[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSIONS_REQUIRED[1])) {
                new AlertDialog.Builder(this).setTitle("Please Accept all the permissions.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(Splash.this, PERMISSIONS_REQUIRED, 100);
                    }
                }).show();
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, 100);
            }
        }

    }

    private void redirect() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        }, 500);
    }
}