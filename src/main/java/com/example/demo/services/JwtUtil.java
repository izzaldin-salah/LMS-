package com.example.demo.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import com.example.demo.tables.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("role", "ROLE_" + user.getType().name()) // Add ROLE_ prefix
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public Long extractUserId(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
        return Long.valueOf(claims.getBody().getSubject());
    }

    public String extractUserRole(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
        return claims.getBody().get("role", String.class);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Missing token");
        }
        return header.substring(7);
    }
}