package org.example.tourismchengdu.security;

public final class UserContext {

    private static final ThreadLocal<LoginUser> CURRENT = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser loginUser) {
        CURRENT.set(loginUser);
    }

    public static LoginUser get() {
        return CURRENT.get();
    }

    public static Long getUserId() {
        LoginUser loginUser = CURRENT.get();
        return loginUser == null ? null : loginUser.getUserId();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
