package com.example.taskmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

// @Component makes the class a Spring component. Spring automatically manages this class.
@Component
public class JwtUtil {

	// Secret key used to sign and verify JWT tokens.
    private String SECRET_KEY = "your_secret_key";
    // Note: the secret key is hardcoded. It would be better to move this key 
    // into application.properties or a environment variable for added security:
    // "jwt.secret.key=your_secret_key".

    // Adding user roles to the attributes (claims) of the token.
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            // Configure the token issue and expiration.
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 ore
            
            // Sign the token with H256.
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            
            .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    // Verify the token user-name and expiration.
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public Claims extractRoles(String token) {
        return extractAllClaims(token);
    }
}
