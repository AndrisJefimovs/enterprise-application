package de.thb.ea.public_transport_tracker.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {

    private final Logger logger = LoggerFactory.getLogger(JwtService.class);
    
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private Long jwtExpiration;

    @Value("${security.jwt.refresh-expiration-time}")
    private Long jwtRefreshExpiration;

    private UserInfoService userInfoService;

    public JwtService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }


    // public interface

    /**
     * This function generates an authorization token for a user.
     * 
     * @param userDetails User details of a specific user.
     * @return The authentication token.
     */
    public String generateToken(UserDetails userDetails) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + jwtExpiration);
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * This function generates a refresh token for an user.
     * 
     * @param user The user to generate the token for.
     * @return The refresh token for the user. If something went wrong it returns null.
     */
    public String generateRefreshToken(User user) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + jwtRefreshExpiration);

        Integer refreshVersion;
        try {
            refreshVersion = userInfoService.nextRefreshVersion(user);
        }
        catch (Exception e) {
            logger.warn(String.format("Failed to update user (id: %d) with new refresh version.",
                                        user.getId()));
            return null;
        }

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("version", refreshVersion);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract the username a token is associatied with.
     * 
     * @param token The (refresh) token you want to extract the username from.
     * @return The username associated with the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract the point of expiration of the token.
     * 
     * @param token The token you want to get the expiration for.
     * @return A Date object with the specific expiration time.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Validates an authentication token for a user.
     * 
     * Do not use this function for validating refresh token. This could lead to false positives.
     * 
     * @param token The token to be validated.
     * @param userDetails The user details of the user how sent the token.
     * @return true if the token is valid for the user, otherwise false.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Validates a refresh token for a specific user.
     * 
     * @param token The refresh token.
     * @param user The user to validate the token for.
     * @return true if the token is valid; otherwise false.
     */
    public Boolean validateRefreshToken(String token, User user) {
        final Integer version = extractVersion(token);
        return (version != null &&
                version == user.getRefreshVersion() &&
                validateToken(token, user));
    }

    // private methods

    private Integer extractVersion(String token) {
        return extractAllClaims(token).get("version", Integer.class);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
}
