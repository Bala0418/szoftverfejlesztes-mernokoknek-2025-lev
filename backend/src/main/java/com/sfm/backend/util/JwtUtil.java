package com.sfm.backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long ttlMillis; // 30 days

    public JwtUtil(@Value("${app.jwt.secret:default_secret_change_me}") String secret,
                   @Value("${app.jwt.ttl-days:30}") long ttlDays) {
        // Use the provided secret (not secure for production if default)
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(bytes.length >= 32 ? bytes : padSecret(bytes));
        this.ttlMillis = ttlDays * 24L * 3600L * 1000L;
    }

    private byte[] padSecret(byte[] bytes) {
        byte[] padded = new byte[32];
        System.arraycopy(bytes, 0, padded, 0, Math.min(bytes.length, 32));
        for (int i = bytes.length; i < 32; i++) padded[i] = (byte) i;
        return padded;
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        Date iat = Date.from(now);
        Date exp = Date.from(now.plusMillis(ttlMillis));
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateAndGetUsername(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return claims.getBody().getSubject();
        } catch (JwtException ex) {
            return null;
        }
    }
}

