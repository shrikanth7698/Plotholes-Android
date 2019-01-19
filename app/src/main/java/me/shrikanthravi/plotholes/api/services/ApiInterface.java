package me.shrikanthravi.plotholes.api.services;

import me.shrikanthravi.plotholes.api.models.general.AutofillRes;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("places/search/json")
    Call<AutofillRes> autfillPlaces(@Header ("Authorization") String authorization, @Query("query") String query);

}