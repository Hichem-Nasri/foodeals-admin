package net.foodeals.authentication.application.services.impl;

import lombok.RequiredArgsConstructor;
import net.foodeals.authentication.application.dtos.requests.LoginRequest;
import net.foodeals.authentication.application.dtos.requests.RegisterRequest;
import net.foodeals.authentication.application.dtos.responses.AuthenticationResponse;
import net.foodeals.authentication.application.services.AuthenticationService;
import net.foodeals.authentication.application.services.JwtService;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;


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
//        final User user = userService.createrequest);
//        return handleRegistration(user);
        return null;
    }

    public AuthenticationResponse login(LoginRequest request) {
        return handleLogin(request);
    }

    private AuthenticationResponse handleRegistration(User user) {
        return getTokens(user);
    }

    private AuthenticationResponse handleLogin(LoginRequest request) {
        System.out.println("here is the login request " + request);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authentication);

        final User user = userService.findByEmail(request.email());
        return getTokens(user);
    }

    private AuthenticationResponse getTokens(User user) {
        final Map<String, Object> extraClaims = Map.of(
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "role", user.getRole().getName()
        );
        return jwtService.generateTokens(user, extraClaims);
    }
}