package com.bach.RoomRentalManagementSystem.security;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtilities {


    @Value("${jwt.secret}")
    private String secret;

    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String email = extractUsername(token);
//        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }

    public Boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        Date now = new Date();
        boolean expired = expiration.before(now);
        log.info("Token expiration: {}, now: {}, isExpired: {}", expiration, now, expired);
        return expired;
    }

    public String generateToken(Map<String, String> parameters, String context, long expirationTime) {
        Instant expirationInstant = Instant.now().plus(expirationTime, ChronoUnit.MILLIS);

        JwtBuilder tokenBuilder = Jwts.builder()
                .setSubject(context)
                .setExpiration(Date.from(expirationInstant))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256, secret);

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            tokenBuilder.claim(entry.getKey(), entry.getValue());
        }

        return tokenBuilder.compact();
    }
    
    public String generateSimpleToken(Map<String, String> parameters, String context) {
        JwtBuilder tokenBuilder = Jwts.builder()
                .setSubject(context)
                .signWith(SignatureAlgorithm.HS256, secret);

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            tokenBuilder.claim(entry.getKey(), entry.getValue());
        }

        return tokenBuilder.compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token. Token expired at: {}", e.getClaims().getExpiration());
            log.trace("Expired JWT token trace: {}", e);
        } catch (SignatureException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }

    public String getToken(HttpServletRequest httpServletRequest) {
        final String bearerToken = httpServletRequest.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

}