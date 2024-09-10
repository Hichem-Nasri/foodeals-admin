package net.foodeals.user.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.user.application.dtos.requests.UserRequest;
import net.foodeals.user.application.dtos.responses.ClientDto;
import net.foodeals.user.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService extends CrudService<User, Integer, UserRequest> {

    User findByEmail(String email);

    User save(User manager);

    Page<User> getClientsData(Pageable page);

    ClientDto toClientDto(User user);
}
