package com.group02.mindmingle.common.security;

import com.group02.mindmingle.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;
    private static final String CLAIM_KEY_USER_ID = "userId";
    private static final String CLAIM_KEY_EMAIL = "email";

    public JwtTokenUtil() {
    }

    private Key getSigningKey() {
        byte[] keyBytes = this.secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap();
        return this.createToken(claims, userDetails.getUsername());
    }

    public String generateToken(UserDetails userDetails, Long userId, String email) {
        Map<String, Object> claims = new HashMap();
        claims.put("userId", userId);
        claims.put("email", email);
        return this.createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + this.expiration * 1000L)).signWith(this.getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = this.extractUsername(token);
        return username.equals(userDetails.getUsername()) && !this.isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return (String)this.extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return (Date)this.extractClaim(token, Claims::getExpiration);
    }

    public Long extractUserId(String token) {
        return (Long)this.extractClaim(token, (claims) -> {
            return (Long)claims.get("userId", Long.class);
        });
    }

    public String extractEmail(String token) {
        return (String)this.extractClaim(token, (claims) -> {
            return (String)claims.get("email", String.class);
        });
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return (Claims)Jwts.parserBuilder().setSigningKey(this.getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    public Boolean validateTokenWithEmail(String token, UserDetails userDetails) {
        String email = this.extractEmail(token);
        return email.equals(((User)userDetails).getEmail()) && !this.isTokenExpired(token);
    }

    private Boolean isTokenExpired(String token) {
        return this.extractExpiration(token).before(new Date());
    }
}
