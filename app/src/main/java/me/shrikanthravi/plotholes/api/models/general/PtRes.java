package me.shrikanthravi.plotholes.api.models.general;

import com.google.gson.annotations.SerializedName;

public class PtRes {

    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lng;

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

}
