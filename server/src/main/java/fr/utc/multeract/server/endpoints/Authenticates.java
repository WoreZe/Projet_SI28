package fr.utc.multeract.server.endpoints;

import fr.utc.multeract.server.dao.request.HistoryLogInRequest;
import fr.utc.multeract.server.dao.request.SignInRequest;
import fr.utc.multeract.server.dao.request.SignUpRequest;
import fr.utc.multeract.server.dao.response.CodeHistoryAuthentification;
import fr.utc.multeract.server.dao.response.JwtAuthenticationResponse;
import fr.utc.multeract.server.exception.UserCreationException;
import fr.utc.multeract.server.services.auth.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/auth", produces = "application/json", consumes = "application/json")
public class Authenticates {
    private static final Logger log = LoggerFactory.getLogger(Authenticates.class);
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public JwtAuthenticationResponse login(@RequestBody SignInRequest request) {
        return authenticationService.signIn(request);
    }

    @PostMapping("/login/history")
    public CodeHistoryAuthentification loginToHistory(@RequestBody HistoryLogInRequest request){
        try {
            return authenticationService.loginToHistory(request);
        } catch (UserCreationException e) {
            log.error("Error while logging in to history", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping(value = "/register")
    public JwtAuthenticationResponse register(@RequestBody SignUpRequest request) {
        try {
            return authenticationService.signup(request);
        } catch (UserCreationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/me")
    public Object me() {
        return authenticationService.me();
    }

    @PostMapping("/refresh")
    public JwtAuthenticationResponse refresh(@RequestBody String token) {
        return authenticationService.refresh(token);
    }
}
