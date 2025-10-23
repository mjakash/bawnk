package com.akash.bawnk.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; // <-- Import @Value
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
	
    private final String secretKeyString;
    private final long jwtExpiration;
    private final SecretKey secretKey; // This will be calculated once in the constructor

    // Inject properties via the constructor
    public JwtService(
            @Value("${application.security.jwt.secret-key}") String secretKeyString,
            @Value("${application.security.jwt.expiration}") long jwtExpiration
    ) {
        this.secretKeyString = secretKeyString;
        this.jwtExpiration = jwtExpiration;
        // Decode the key once and store it
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(this.secretKeyString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }
    // --- END OF NEW ---

    // 2. Extracts the username from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    // ... (extractClaim method is unchanged) ...
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 4. Generates a token from UserDetails
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // 5. Generates a token with extra claims (MODIFIED)
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                // --- MODIFIED: Use the injected expiration time ---
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) 
                .signWith(getSigningKey()) // <-- Use the getSigningKey() method
                .compact();
    }

    // 6. Validates the token (unchanged)
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // 7. Checks if the token is expired (unchanged)
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // --- Helper Methods ---

    // Decodes the token to get all claims (MODIFIED)
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                // --- MODIFIED: Use the getSigningKey() method ---
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Gets the signing key (MODIFIED)
    private SecretKey getSigningKey() {
        // --- MODIFIED: Return the pre-calculated key ---
        return this.secretKey;
    }
}