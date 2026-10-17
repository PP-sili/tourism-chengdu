package org.example.tourismchengdu.security;

public class LoginUser {

    private final Long userId;
    private final String phone;

    public LoginUser(Long userId, String phone) {
        this.userId = userId;
        this.phone = phone;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPhone() {
        return phone;
    }
}
