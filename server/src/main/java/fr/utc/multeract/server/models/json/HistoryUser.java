package fr.utc.multeract.server.models.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.utc.multeract.server.dao.request.HistoryLogInRequest;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HistoryUser {
    private String avatar;
    private String username;

    private String uniqueId;

    private CurrentAnswer currentResponse;

    public HistoryUser() {
    }

    public HistoryUser(String avatar, String username, String uniqueId) {
        this.avatar = avatar;
        this.username = username;
        this.uniqueId = uniqueId;
    }

    public HistoryUser(HistoryLogInRequest request) {
        this.avatar = request.avatar();
        this.username = request.username();
        this.uniqueId = generateUniqueId();
    }

    private String generateUniqueId() {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String s = this.username + this.avatar + System.currentTimeMillis();
        m.update(s.getBytes(), 0, s.length());
        return new BigInteger(1, m.digest()).toString(16);
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public CurrentAnswer getCurrentResponse() {
        return currentResponse;
    }

    public void setCurrentResponse(CurrentAnswer currentResponse) {
        this.currentResponse = currentResponse;
    }

    //set current response value
    public void setCurrentResponseValue(Object value) {
        if (this.currentResponse == null) {
            this.currentResponse = new CurrentAnswer();
        }
        this.currentResponse.setValue(value);
    }

    @JsonProperty( access = JsonProperty.Access.READ_ONLY)
    public boolean hasResponse() {
        return this.currentResponse != null;
    }

    @JsonProperty( access = JsonProperty.Access.READ_ONLY)
    public boolean hasResponseValue() {
        if(this.currentResponse == null){
            return false;
        }
        return this.currentResponse.getValue() != null;
    }

    @JsonIgnore
    public void clearResponse() {
        this.currentResponse = null;
    }
}
