package net.foodeals.authentication.application.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.foodeals.authentication.application.dtos.requests.LoginRequest;
import net.foodeals.authentication.application.dtos.requests.RegisterRequest;
import net.foodeals.authentication.application.dtos.responses.AuthenticationResponse;
import net.foodeals.authentication.application.dtos.responses.LoginResponse;
import net.foodeals.authentication.application.services.AuthenticationService;
import net.foodeals.authentication.application.services.JwtService;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.repositories.OrganizationEntityRepository;
import net.foodeals.user.application.dtos.requests.UserRequest;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**

 AuthenticationServiceImpl */
@Service
@RequiredArgsConstructor
@Slf4j
class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OrganizationEntityRepository organizationEntityRepository;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        // OrganizationEntity organizationEntity = this.organizationEntityRepository.findByName("manager test");
        final User user = userService.create(new UserRequest(request.name(), request.email(), request.phone(), request.password(), request.isEmailVerified(), request.roleName(), null));
        return handleRegistration(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authentication);

            final User user = userService.findByEmail(request.email());

            // Generate token (assuming getTokens returns a token string)
            AuthenticationResponse token = getTokens(user);

            // Create and return the LoginResponse
            return new LoginResponse(
                    user.getName(),
                    user.getRole().getName(), // Assuming Role has a method getName()
                    user.getAvatarPath(), // Avatar path
                    user.getId(), // User ID
                    token // Token
            );

        } catch (Exception e) {
            log.error("Login failed for user: {}", request.email(), e);
            throw e; // Consider throwing a custom exception for better error handling
        }
    }

    @Transactional
    public boolean verifyToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return jwtService.isTokenValid(token, userDetails);
        } catch (Exception e) {
            log.error("Token verification failed: {}", e.getMessage());
            return false;
        }
    }

    @Transactional
    private AuthenticationResponse handleRegistration(User user) {
        return getTokens(user);
    }

    @Transactional
    private AuthenticationResponse getTokens(User user) {
        final Map<String, Object> extraClaims = Map.of(
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "role", user.getRole().getName()
        );
        return jwtService.generateTokens(user, extraClaims);
    }
}