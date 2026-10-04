package com.courserec.coursesystem.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordUtils {
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordUtils() {}

    public static String hash(String password) {
        return ENCODER.encode(password);
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) return false;
        if (isHashed(storedPassword)) return ENCODER.matches(rawPassword, storedPassword);
        // 兼容已有演示数据；用户下次登录时升级为 BCrypt。
        return rawPassword.equals(storedPassword);
    }

    public static boolean isHashed(String password) {
        return password != null && password.matches("^\\$2[aby]\\$\\d{2}\\$.*");
    }
}
