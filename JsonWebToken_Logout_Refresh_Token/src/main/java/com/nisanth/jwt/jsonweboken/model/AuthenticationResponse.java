package com.nisanth.jwt.jsonweboken.model;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationResponse {
    @JsonProperty("access_token") // get output in postman
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;


    public AuthenticationResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;

        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

}