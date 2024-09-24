package fr.utc.multeract.server.services;

import fr.utc.multeract.server.middleware.UserTokenDetails;
import fr.utc.multeract.server.models.StoryInstance;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequestScope
public class PermissionManager {
    private static final Logger log = LoggerFactory.getLogger(PermissionManager.class);

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public Claims getAuthenticationClaims() {
        return ((UserTokenDetails) getAuthentication().getPrincipal()).getClaims();
    }

    public boolean isHistoryUser(){
        var claims = getAuthenticationClaims();
        return claims.get("type").equals("history-token");
    }

    boolean isAccessUser(){
        var claims = getAuthenticationClaims();
        return claims.get("type").equals("access-token");
    }

    public void accessHistoryInstance(StoryInstance instance) {
        var claims = getAuthenticationClaims();
        if (isHistoryUser()){
            if(instance.getId().equals(Long.valueOf(claims.get("instance").toString()))){
               return;
            }
        }
        if (isAccessUser()){
            if(instance.getGameOwner().getUsername().equals(claims.get("user"))){
                return;
            }
        }
        throw new HttpServerErrorException(HttpStatus.FORBIDDEN,"You are not allowed to access this history instance");
    }

    public String getHistoryUserName(){
        var claims = getAuthenticationClaims();
        if (isHistoryUser()){
            return (String) claims.get("user");
        }
        throw new HttpServerErrorException(HttpStatus.FORBIDDEN,"You are not allowed to access this history instance");
    }

    @ExceptionHandler(SecurityException.class)
    public void handleSecurityException(SecurityException e) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,e.getMessage());
    }

    public Long getInstanceId() {
        var claims = getAuthenticationClaims();
        if (isHistoryUser()){
            return Long.valueOf(claims.get("instance").toString());
        }
        throw new HttpServerErrorException(HttpStatus.FORBIDDEN,"You are not allowed to access this history instance");
    }
}
