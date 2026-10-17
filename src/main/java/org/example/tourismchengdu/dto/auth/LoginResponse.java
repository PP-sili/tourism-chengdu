package org.example.tourismchengdu.dto.auth;

import org.example.tourismchengdu.dto.user.UserProfileVO;

public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private UserProfileVO user;

    public LoginResponse() {
    }

    public LoginResponse(String token, UserProfileVO user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UserProfileVO getUser() {
        return user;
    }

    public void setUser(UserProfileVO user) {
        this.user = user;
    }
}
