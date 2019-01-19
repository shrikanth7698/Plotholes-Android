package me.shrikanthravi.plotholes.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlotholeData {
    @Expose
    @SerializedName("pothhole")
    boolean pothhole=false;
    @Expose
    @SerializedName("speedbreaker")
    boolean speedbreaker=false;
    @Expose
    @SerializedName("acc_x")
    String acc_x;
    @Expose
    @SerializedName("acc_y")
    String acc_y;
    @Expose
    @SerializedName("acc_z")
    String acc_z;
    @Expose
    @SerializedName("gyro_x")
    String gyro_x;
    @Expose
    @SerializedName("gyro_y")
    String gyro_y;
    @Expose
    @SerializedName("gyro_z")
    String gyro_z;
    @Expose
    @SerializedName("timestamp")
    String timestamp;
    @Expose
    @SerializedName("lat")
    double lat;
    @Expose
    @SerializedName("lng")
    double lng;

    public boolean isPothhole() {
        return pothhole;
    }

    public void setPothhole(boolean pothhole) {
        this.pothhole = pothhole;
    }

    public boolean isSpeedbreaker() {
        return speedbreaker;
    }

    public void setSpeedbreaker(boolean speedbreaker) {
        this.speedbreaker = speedbreaker;
    }

    public String getAcc_x() {
        return acc_x;
    }

    public void setAcc_x(String acc_x) {
        this.acc_x = acc_x;
    }

    public String getAcc_y() {
        return acc_y;
    }

    public void setAcc_y(String acc_y) {
        this.acc_y = acc_y;
    }

    public String getAcc_z() {
        return acc_z;
    }

    public void setAcc_z(String acc_z) {
        this.acc_z = acc_z;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getGyro_x() {
        return gyro_x;
    }

    public void setGyro_x(String gyro_x) {
        this.gyro_x = gyro_x;
    }

    public String getGyro_y() {
        return gyro_y;
    }

    public void setGyro_y(String gyro_y) {
        this.gyro_y = gyro_y;
    }

    public String getGyro_z() {
        return gyro_z;
    }

    public void setGyro_z(String gyro_z) {
        this.gyro_z = gyro_z;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
