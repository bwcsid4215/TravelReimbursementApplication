package com.rentkaro.userservice.security;

import com.rentkaro.userservice.entity.Role;
import com.rentkaro.userservice.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${rentkaro.app.jwtSecret}")
    private String jwtSecret;

    @Value("${rentkaro.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * ✅ Generate JWT Token (with Roles + UUID + Expiry)
     */
    public String generateJwtToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();

        // ✅ Extract roles as list of strings
        List<String> roles = userPrincipal.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // ✅ Build JWT using JJWT 0.12.7 API
        return Jwts.builder()
                .claims()
                .subject(userPrincipal.getUsername()) // ✅ set subject
                .add("userId", userPrincipal.getId().toString())
                .add("email", userPrincipal.getEmail())
                .add("roles", roles) // ✅ include roles
                .issuedAt(now)
                .expiration(expiryDate)
                .id(UUID.randomUUID().toString())
                .and()
                .signWith(getSignKey(), Jwts.SIG.HS512) // ✅ secure signing
                .compact();
    }

    /**
     * ✅ Extract Username from JWT
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

//    /**
//     * ✅ Extract User ID (UUID) from JWT
//     */
//    public UUID getUserIdFromJwtToken(String token) {
//        String id = Jwts.parser()
//                .verifyWith(getSignKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .getId();
//        return UUID.fromString(id);
//    }

//    /**
//     * ✅ Extract Roles safely from JWT (Type-Safe)
//     */
//    public List<String> getRolesFromJwtToken(String token) {
//        Object rolesObj = Jwts.parser()
//                .verifyWith(getSignKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .get("roles");
//
//        // ✅ Safe check and convert to List<String>
//        if (rolesObj instanceof List<?>) {
//            return ((List<?>) rolesObj).stream()
//                    .map(Object::toString)
//                    .collect(Collectors.toList());
//        }
//
//        return Collections.emptyList(); // ✅ If no roles, return empty list
//    }

    /**
     * ✅ Validate JWT Token with proper logging
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("❌ JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("❌ JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("❌ Invalid JWT token: {}", e.getMessage());
        } catch (SecurityException e) {
            logger.error("❌ Invalid JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("❌ JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * ✅ Generate Signing Key (HS512)
     */
    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
