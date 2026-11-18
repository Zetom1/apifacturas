package com.moocode.apifacturas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class JwtService {
    private final SecretKey signingKey;
    private final long accessTokenMinutes;

    public JwtService(@Value("${app.security.jwt.secret}") String secret,
                      @Value("15") long accessTokenMinutes) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("La clave JWT HS256 debe tener al menos 32 bytes.");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenMinutes = accessTokenMinutes;
    }

    public String createAccessToken(UserDetails user) {
        Instant now = Instant.now();
        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("authorities", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenMinutes * 60)))
                .signWith(signingKey)
                .compact();
    }

    public String extractSubject(String token) { return parse(token).getSubject(); }

    public boolean isValid(String token, UserDetails user) {
        Claims claims = parse(token);
        return claims.getSubject().equalsIgnoreCase(user.getUsername())
                && claims.getExpiration().after(new Date());
    }

    public long getAccessTokenLifetimeSeconds() { return accessTokenMinutes * 60; }

    private Claims parse(String token) {
            return Jwts.parser().verifyWith(signingKey).build()
                    .parseSignedClaims(token).getPayload();

    }
}
