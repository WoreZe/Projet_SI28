package fr.utc.multeract.server.services.auth;

import fr.utc.multeract.server.dao.response.CodeHistoryAuthentification;
import fr.utc.multeract.server.models.StoryInstance;
import fr.utc.multeract.server.models.Roles;
import fr.utc.multeract.server.models.User;
import fr.utc.multeract.server.models.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @Value("${token.access.time}")
    private long expirationAccessTime = 3600;

    @Value("${token.refresh.time}")
    private long expirationRefreshTime = 3600 * 24 * 7;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(String context, User userDetails) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access-token");
        claims.put("audience", context);
        return generateToken(claims, userDetails, expirationAccessTime);
    }

    public String generateRefreshToken(String context, User userDetails) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh-token");
        claims.put("audience", context);
        return generateToken(claims, userDetails, expirationRefreshTime);
    }

    public boolean isTokenValid(String token) {
        try{
            return extractAllClaims(token).getIssuer().equals("multeract-api");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        return extractClaim(token, (claims -> claims.get("type"))).equals("refresh-token");
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, User user, long expirationTime) {
        String aud = "multeract";
        if (extraClaims.containsKey("audience")) {
            aud = (String) extraClaims.get("audience");
        }
        List<String> roles = user.getRoles().contains(UserRole.ADMIN) ? List.of(Roles.ADMIN) : List.of(Roles.USER);
        extraClaims.put("roles", roles);
        extraClaims.put("user", user.getUsername());
        return Jwts.builder().setClaims(extraClaims).setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * expirationTime))
                .setAudience(aud)
                .setIssuer("multeract-api")
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractContext(String token) {
        return extractClaim(token, Claims::getAudience);
    }

    public CodeHistoryAuthentification createTokenForHistory(StoryInstance instance, String username, boolean readOnly) {
        List<String> roles = new ArrayList<>();
        if (readOnly) {
            roles.add(Roles.HISTORY_INSTANCE_SCREEN);
        } else {
            roles.add(Roles.HISTORY_INSTANCE_USER);
        }
        Map<String,Object> claims = new HashMap<>();
        claims.put("history", instance.getHistory().getId());
        claims.put("instance", instance.getId());
        claims.put("roles", roles);
        claims.put("user", username);
        claims.put("type", "history-token");
        //Subject is the instance id and the username
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(instance.getId() + "-" + username + (readOnly ? "-readonly" : ""))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * expirationAccessTime))
                .setIssuer("multeract-api")
                .setAudience(readOnly ? "history-session-screen" : "history-session-user")
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
        return new CodeHistoryAuthentification(token);
    }

    public Long extractHistoryInstanceId(String token) {
        //if type is not history-token, return null
        if (!extractClaim(token, (claims -> claims.get("type"))).equals("history-token")) {
            return null;
        }
        return Long.parseLong(extractClaim(token, (claims -> claims.get("instance"))).toString());
    }
}