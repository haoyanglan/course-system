package com.courserec.coursesystem.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.UUID;

public class JwtUtils {

    // 未配置持久密钥时，每次启动生成随机密钥，避免公开仓库中的固定密钥被用于伪造令牌。
    private static final String SECRET =
            System.getenv().getOrDefault("JWT_SECRET", UUID.randomUUID().toString() + UUID.randomUUID());
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

    public static DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
    }
}
