package com.astraval.backend.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static final String TYPE_CLAIM  = "type";
    private static final String ACCESS      = "access";
    private static final String REFRESH     = "refresh";
    private static final String ORG_CLAIM   = "orgId";
    private static final String ROLES_CLAIM = "roles";

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

    /** Subject = userId; claims: orgId, roles, type=access */
    public String generateAccessToken(String userId, String orgId, List<String> roles) {
        return Jwts.builder()
                .subject(userId)
                .claim("userId", userId)
                .claim(ORG_CLAIM, orgId)
                .claim(ROLES_CLAIM, roles)
                .claim(TYPE_CLAIM, ACCESS)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /** Subject = userId; type=refresh */
    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .claim(TYPE_CLAIM, REFRESH)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    // ── Extraction ────────────────────────────────────────────────────────────

    public String extractUserId(String token) {
        return getClaims(token).getSubject();
    }

    public String extractOrgId(String token) {
        return getClaims(token).get(ORG_CLAIM, String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object raw = getClaims(token).get(ROLES_CLAIM);
        if (raw instanceof List<?> list) {
            return (List<String>) list;
        }
        return List.of();
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
