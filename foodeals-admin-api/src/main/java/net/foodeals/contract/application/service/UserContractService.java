package net.foodeals.contract.application.service;

import net.foodeals.contract.domain.entities.UserContract;
import net.foodeals.contract.domain.repositories.UserContractRepository;
import net.foodeals.organizationEntity.application.dtos.requests.UpdateOrganizationEntityDto;
import net.foodeals.user.application.services.UserService;
import net.foodeals.user.domain.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class UserContractService {

    private final UserContractRepository userContractRepository;
    private final UserService userService;


    public UserContractService(UserContractRepository userContractRepository, UserService userService) {
        this.userContractRepository = userContractRepository;
        this.userService = userService;
    }

    public List<UserContract> getUserContracts() {
        return this.userContractRepository.findAll();
    }

    public UserContract getUserContractById(UUID id) {
        UserContract userContract =  this.userContractRepository.findById(id).orElse(null);

        if (userContract == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "UserContract not found");
        }

        return userContract;
    }

    public UserContract save(UserContract userContract) {
        return this.userContractRepository.save(userContract);
    }

    public UserContract updateUserContract(UserContract userContract, UpdateOrganizationEntityDto updateOrganizationEntityDto) {
        User oldUser = userContract.getUser();
        oldUser.getUserContracts().remove(userContract);
            User user = this.userService.findById(updateOrganizationEntityDto.getManagerId());
            userContract.setUser(user);
        return this.userContractRepository.save(userContract);
    }
}
