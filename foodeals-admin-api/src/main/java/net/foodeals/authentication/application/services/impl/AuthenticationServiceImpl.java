package net.foodeals.authentication.application.services.impl;

import lombok.RequiredArgsConstructor;
import net.foodeals.authentication.application.dtos.requests.AuthRequest;
import net.foodeals.authentication.application.dtos.requests.LoginRequest;
import net.foodeals.authentication.application.dtos.requests.RegisterRequest;
import net.foodeals.authentication.application.dtos.responses.AuthenticationResponse;
import net.foodeals.authentication.application.services.AuthenticationService;
import net.foodeals.authentication.application.services.JwtService;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * AuthenticationServiceImpl
 */
@Service
@RequiredArgsConstructor
class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        final User user = userService.save(request);
        return handleAuthentication(user, request);
    }

    public AuthenticationResponse login(LoginRequest request) {
        final User user = userService.findByEmail(request.email());
        return handleAuthentication(user, request);
    }

    private AuthenticationResponse handleAuthentication(User user, AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));

//        final Map<String, Object> extraClaims = Map.of(
//                "authorities", user.getAuthorities(),
//                "email", user.getEmail(),
//                "createdAt", user.getCreatedAt()
//        );
        return jwtService.generateTokens(user);
    }
}
