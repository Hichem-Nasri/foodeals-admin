package net.foodeals.user.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.user.application.dtos.requests.UserRequest;
import net.foodeals.user.domain.entities.User;

public interface UserService extends CrudService<User, Integer, UserRequest> {

    User findByEmail(String email);
}
