package fr.utc.multeract.server.services.auth;

import fr.utc.multeract.server.dao.request.HistoryLogInRequest;
import fr.utc.multeract.server.dao.request.SignInRequest;
import fr.utc.multeract.server.dao.request.SignUpRequest;
import fr.utc.multeract.server.dao.response.CodeHistoryAuthentification;
import fr.utc.multeract.server.dao.response.JwtAuthenticationResponse;
import fr.utc.multeract.server.exception.UserCreationException;
import fr.utc.multeract.server.models.StoryInstance;
import fr.utc.multeract.server.models.User;
import fr.utc.multeract.server.models.UserRole;
import fr.utc.multeract.server.models.json.HistoryUser;
import fr.utc.multeract.server.repositories.HistoryInstanceRepository;
import fr.utc.multeract.server.repositories.UserRepository;
import fr.utc.multeract.server.services.PermissionManager;
import fr.utc.multeract.server.websocket.events.HistoryInstanceStateMessage;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final HistoryInstanceRepository historyInstanceRepository;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private PermissionManager permissionManager;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, HistoryInstanceRepository historyInstanceRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.historyInstanceRepository = historyInstanceRepository;
    }

    public JwtAuthenticationResponse signup(SignUpRequest request) throws UserCreationException {
        //check if email is already used
        if (userRepository.existsByEmail(request.email())) {
            throw new UserCreationException("Email already used");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new UserCreationException("Username already used");
        }
        var user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(request.username());
        user.setRoles(List.of(UserRole.USER));
        userRepository.save(user);
        return this.getTokensResponse("web-app", user);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        return this.getTokensResponse("web-app", user);
    }

    public JwtAuthenticationResponse refresh(String token) {
        if (!jwtService.isTokenValid(token) || !jwtService.isRefreshToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Outdated or invalid token");
        }
        var userName = jwtService.extractUserName(token);
        var context = jwtService.extractContext(token);
        var user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return this.getTokensResponse(context, user);
    }

    private JwtAuthenticationResponse getTokensResponse(String context, User user) {
        var jwt = jwtService.generateToken(context, user);
        var refreshToken = jwtService.generateRefreshToken(context, user);
        return new JwtAuthenticationResponse(jwt, refreshToken);
    }

    public CodeHistoryAuthentification loginToHistory(HistoryLogInRequest request) throws UserCreationException {
        StoryInstance instance = this.historyInstanceRepository.findByJoinCode(request.code()).orElseThrow(() -> new BadCredentialsException("Unable to find history session with this code"));
        if(request.screen()){
            return jwtService.createTokenForHistory(instance, "screen", true);
        }
        switch (instance.getState()) {
            case WAITING_FOR_PLAYERS -> {
                if (instance.getUsers().size() < instance.getHistory().getMaxPlayers() || instance.getUsers().containsKey(request.username())) {
                    if(instance.getUsers().containsKey(request.username())){
                        instance.getUsers().remove(request.username());
                    }
                    instance.addUser(new HistoryUser(request));
                    this.historyInstanceRepository.save(instance);
                    this.applicationEventPublisher.publishEvent(new HistoryInstanceStateMessage(instance));
                    log.info("User {} joined history session {}", request.username(), instance.getId());
                    return jwtService.createTokenForHistory(instance, request.username(), false);
                } else {
                    throw new UserCreationException("This history session is full");
                }
            }
            case IN_GAME -> {
                if (instance.getUsers().containsKey(request.username())) {
                    return jwtService.createTokenForHistory(instance, request.username(), false);
                } else {
                    throw new UserCreationException("This history session is in game, impossible to join it with new username");
                }
            }
            case DONE -> {
                throw new UserCreationException("This history session is done, impossible to join it");
            }
        }
        throw new UserCreationException("Unable to join this history session");
    }

    public boolean isRefreshToken(String token) {
        return jwtService.isRefreshToken(token);
    }

    public User getUser(String token) {
        return userRepository.findByEmail(jwtService.extractUserName(token)).orElse(null);
    }

    public Map<String,Object> me() {
        Claims claims = permissionManager.getAuthenticationClaims();
        if(permissionManager.isHistoryUser()){
            return Map.of("type","history","user",permissionManager.getHistoryUserName());
        }
        return Map.of("type","access","user",claims.get("user"));
    }
}
