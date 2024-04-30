package com.webdev.backend.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.webdev.backend.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: Jwt工具类，生成JWT和认证
 * @author: heshi
 */
public class JwtUtil {

	private static final String SECRET = "my_secret";

	private static final long EXPIRATION = 18000L;// 单位为秒

	public static String createToken(User user) {
		Date expireDate = new Date(System.currentTimeMillis() + EXPIRATION * 1000);
		Map<String, Object> map = new HashMap<>();
		map.put("alg", "HS256");
		map.put("typ", "JWT");
		String token = JWT.create()
			.withHeader(map)
			.withClaim("id", user.getId())
			.withClaim("username", user.getUsername())
			.withClaim("password", user.getPassword())
			.withClaim("role", user.getRole())
			.withExpiresAt(expireDate)
			.withIssuedAt(new Date())
			.sign(Algorithm.HMAC256(SECRET));
		return token;
	}

	public static Map<String, Claim> verifyToken(String token) {
		DecodedJWT jwt = null;
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
			jwt = verifier.verify(token);
		}
		catch (Exception e) {
			return null;
		}
		return jwt.getClaims();
	}

}
