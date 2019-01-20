package me.shrikanthravi.plotholes.api.services;

import java.util.List;

import me.shrikanthravi.plotholes.api.models.general.AutofillRes;
import me.shrikanthravi.plotholes.api.models.general.RoutingRes;
import me.shrikanthravi.plotholes.data.models.PotholeLocation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("places/search/json")
    Call<AutofillRes> autfillPlaces(@Header ("Authorization") String authorization, @Query("query") String query);

    @FormUrlEncoded
    @POST("pothole/add")
    Call<ResponseBody> uploadPlotHole(@Field("latitude") String lat,@Field("longitude") String lng);

    @GET("all/potholes")
    Call<List<PotholeLocation>> getPotholes();

}