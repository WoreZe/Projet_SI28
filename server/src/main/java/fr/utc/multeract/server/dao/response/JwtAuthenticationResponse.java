package fr.utc.multeract.server.dao.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JwtAuthenticationResponse {
    @JsonProperty("access_token")
    private final String accessToken;

    @JsonProperty("refresh_token")
    private final String refreshToken;

    @JsonProperty("type")
    private final String type = "Bearer";

    public JwtAuthenticationResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getType() {
        return type;
    }
}

