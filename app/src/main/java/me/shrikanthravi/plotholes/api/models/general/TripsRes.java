package me.shrikanthravi.plotholes.api.models.general;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TripsRes {

    @SerializedName("advices")
    private List<PtRes> advices;

    public List<PtRes> getAdvices() {
        return advices;
    }

}
