package net.foodeals.user.application.services;

import net.foodeals.authentication.application.dtos.requests.RegisterRequest;
import net.foodeals.user.domain.entities.User;

public interface UserService {
    User save(RegisterRequest request);

    User findByEmail(String email);
}
