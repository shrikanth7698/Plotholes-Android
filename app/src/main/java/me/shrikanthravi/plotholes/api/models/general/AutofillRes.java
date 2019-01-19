package me.shrikanthravi.plotholes.api.models.general;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AutofillRes {

    @SerializedName("suggestedLocations")
    private List<SuggestedLocationsRes> suggestedLocations;

    public List<SuggestedLocationsRes> suggestedLocationsRes() {
        return suggestedLocations;
    }

}
