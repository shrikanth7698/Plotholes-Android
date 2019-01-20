package me.shrikanthravi.plotholes.api.models.general;

import com.google.gson.annotations.SerializedName;

public class RoutingRes {

    @SerializedName("version")
    private String version;

    @SerializedName("results")
    private ResultsRes results;

    public String getVersion() {
        return version;
    }

    public ResultsRes getResults() {
        return results;
    }

}
