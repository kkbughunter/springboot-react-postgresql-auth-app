package com.astraval.backend.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String TYPE_CLAIM = "type";
    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    // ── Token generation ──────────────────────────────────────────────────────

    public String generateAccessToken(String email, String userId) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim(TYPE_CLAIM, ACCESS)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .claim(TYPE_CLAIM, REFRESH)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    // ── Extraction ────────────────────────────────────────────────────────────

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String extractUserId(String token) {
        return getClaims(token).get("userId", String.class);
    }

    // ── Validation ────────────────────────────────────────────────────────────

    public boolean isTokenValid(String token) {
        try {
            return !getClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            return ACCESS.equals(getClaims(token).get(TYPE_CLAIM, String.class));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            return REFRESH.equals(getClaims(token).get(TYPE_CLAIM, String.class));
        } catch (Exception e) {
            return false;
        }
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
