package me.shrikanthravi.plotholes.api.services;

import me.shrikanthravi.plotholes.api.models.oauth.OAuthRes;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OAuthInterface {

    @FormUrlEncoded
    @POST("security/oauth/token")
    Call<OAuthRes> getAccessToken(@Field("grant_type") String grant_type, @Field("client_id") String client_id, @Field("client_secret") String client_secret);

}
