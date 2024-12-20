package net.foodeals.authentication.application.services;

import net.foodeals.authentication.application.dtos.requests.LoginRequest;
import net.foodeals.authentication.application.dtos.requests.RegisterRequest;
import net.foodeals.authentication.application.dtos.responses.AuthenticationResponse;
import net.foodeals.authentication.application.dtos.responses.LoginResponse;

/**
 * AuthenticationService
 */
public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request) throws Exception;

    LoginResponse login(LoginRequest request);

    boolean verifyToken(String token);
}
