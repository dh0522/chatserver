package com.example.chatserver.common.auth;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider { // token 생성 해주는 클래스에용

	private final String secretKey;
	private final int expiration;
	private Key SECRET_KEY;

	public JwtTokenProvider(@Value("${jwt.secretKey}") String secretKey, @Value("${jwt.expiration}") int expiration) {
		this.secretKey = secretKey;
		this.expiration = expiration;
		// secret key를 디코딩 시킨 후, 완전히 암호화시키겠다. -> 그럼 더 이상 디코딩 할 수 없음.
		this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
	}

	public String createToken(String email, String role){
		Claims claims = Jwts.claims().setSubject(email);
		claims.put("role", role);

		Date now = new Date();
		String token = Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)						// 3000분 * 60초 * 1000msec
			.setExpiration( new Date(now.getTime() + expiration*60*1000L ))
			.signWith(SECRET_KEY)
			.compact();

		return token;
	}
}
