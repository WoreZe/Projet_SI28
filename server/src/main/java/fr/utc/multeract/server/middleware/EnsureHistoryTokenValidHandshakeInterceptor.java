package fr.utc.multeract.server.middleware;

import fr.utc.multeract.server.services.auth.AuthenticationService;
import fr.utc.multeract.server.services.auth.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EnsureHistoryTokenValidHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String q = request.getURI().getQuery();
        if(q == null) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }
        Map<String, Optional<String>> params = Arrays.stream(q.split("&")).map(s -> s.split("=")).collect(Collectors.toMap(s -> s[0], s -> s.length > 1 ? Optional.of(s[1]) : Optional.empty()));
        Optional<String> tokenOpt = params.getOrDefault("token", Optional.empty());
        if(tokenOpt.isEmpty()) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }
        try {
            String token = tokenOpt.get();
            //check if token is valid
            if(!jwtService.isTokenValid(token)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }
            Claims claims = jwtService.extractAllClaims(token);
            if(!claims.get("type").equals("history-token")) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }
            Long historyId = Long.parseLong(claims.get("history").toString());
            boolean isReadOnly = claims.getSubject().contains("readonly");
            //add user to attributes
            attributes.put("history_id", historyId);
            attributes.put("instance",  Long.parseLong(claims.get("instance").toString()));
            attributes.put("username", claims.get("user"));
            attributes.put("readonly", isReadOnly);
            return true;
        } catch (JwtException e) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
