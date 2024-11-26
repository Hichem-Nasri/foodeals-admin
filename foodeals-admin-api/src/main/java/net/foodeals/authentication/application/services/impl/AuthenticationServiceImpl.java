package net.foodeals.authentication.application.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.foodeals.authentication.application.dtos.requests.LoginRequest;
import net.foodeals.authentication.application.dtos.requests.RegisterRequest;
import net.foodeals.authentication.application.dtos.responses.AuthenticationResponse;
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
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * AuthenticationServiceImpl
 */
@Service
@RequiredArgsConstructor
@Slf4j
class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OrganizationEntityRepository organizationEntityRepository;

    public AuthenticationResponse register(RegisterRequest request) {
//        OrganizationEntity organizationEntity = this.organizationEntityRepository.findByName("manager test");
        final User user = userService.create(new UserRequest(request.name(), request.email(), request.phone(), request.password(), request.isEmailVerified(), request.roleName(), null));
        return handleRegistration(user);
    }

    public AuthenticationResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()));

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authentication);

            final User user = userService.findByEmail(request.email());

            AuthenticationResponse response = getTokens(user);

            return response;
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.email(), e);
            e.printStackTrace();
            throw e;
        }
    }

    private AuthenticationResponse handleRegistration(User user) {
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