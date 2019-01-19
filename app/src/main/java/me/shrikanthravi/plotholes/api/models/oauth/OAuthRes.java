package me.shrikanthravi.plotholes.api.models.oauth;

import com.google.gson.annotations.SerializedName;

public class OAuthRes {

    @SerializedName("access_token")
    private String access_token;

    @SerializedName("token_type")
    private String token_type;

    @SerializedName("expires_in")
    private int expires_in;

    @SerializedName("scope")
    private String scope;

    @SerializedName("project_name")
    private String project_name;

    public String getAccessToken() {
        return access_token;
    }

    public String getTokenType() {
        return token_type;
    }

    public int getExpiresIn() {
        return expires_in;
    }

    public String getScope() {
        return scope;
    }

    public String getProjectName() {
        return project_name;
    }

}
