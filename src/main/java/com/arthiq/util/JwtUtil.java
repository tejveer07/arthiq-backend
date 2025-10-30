package com.arthiq.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component  // <--- Add this annotation
public class JwtUtil {
    private final String jwtSecret = "YourSuperSecretKeyForJWTSigningMustBeAtLeast64CharactersLongForHS512AlgorithmAndThereIsaLotofDiscontnentamongus";
    private final long jwtExpirationMs = 315360000000L; // 1 hour

    public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Log or handle the exception
        }
        return false;
    }
}
