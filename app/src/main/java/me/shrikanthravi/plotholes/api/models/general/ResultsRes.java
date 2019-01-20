package me.shrikanthravi.plotholes.api.models.general;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ResultsRes {

    @SerializedName("trips")
    private List<TripsRes> trips;

    @SerializedName("duration")
    private int duration;

    public List<TripsRes> getTrips() {
        return trips;
    }

    public int getDuration() {
        return duration;
    }

}
