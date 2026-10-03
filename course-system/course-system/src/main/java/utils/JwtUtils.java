package com.courserec.coursesystem.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;

public class JwtUtils {

    // JWT 签名密钥：优先读取环境变量 JWT_SECRET，未配置时使用演示默认值。
    // 生产环境请务必通过环境变量注入强随机值。
    private static final String SECRET =
            System.getenv().getOrDefault("JWT_SECRET", "CourseSystemMyAwesomeSecretKey2026");
    // 通行证的有效时间（这里设置的是 24 小时）
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    // 颁发通行证（把用户的角色和账号塞进 Token 里）
    public static String generateToken(String username, String role) {
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return JWT.create()
                .withClaim("username", username)
                .withClaim("role", role) // 告诉前端这是什么角色
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC256(SECRET));
    }
}
