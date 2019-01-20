package me.shrikanthravi.plotholes.api.services;

import com.google.gson.JsonElement;

import me.shrikanthravi.plotholes.api.models.general.RoutingRes;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RoutingInteface {

    @GET("{licensekey}/route")
    Call<JsonElement> getRouteWaypoints(@Path("licensekey") String licensekey, @Query("start") String start, @Query("destination") String destination, @Query("alternatives") String alternatives, @Query("with_advices") String with_advices);

}
