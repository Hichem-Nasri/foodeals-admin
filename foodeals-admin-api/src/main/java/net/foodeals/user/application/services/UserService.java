package net.foodeals.user.application.services;

import net.foodeals.common.contracts.CrudService;
import net.foodeals.common.dto.request.UpdateReason;
import net.foodeals.common.dto.response.UpdateDetails;
import net.foodeals.user.application.dtos.requests.UserRequest;
import net.foodeals.user.application.dtos.responses.*;
import net.foodeals.user.domain.entities.Role;
import net.foodeals.user.domain.entities.User;
import net.foodeals.user.domain.entities.WorkingHours;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService extends CrudService<User, Integer, UserRequest> {

    User findByEmail(String email);

    User save(User manager);

    Page<User> getClientsData(Pageable page);

    Page<User> filterUsers(UserFilter filter, Pageable pageable);

    ClientDto toClientDto(User user);

    Long countDeliveryUsersByOrganizationId(UUID id);

    Page<User> getOrganizationUsers(Pageable pageable, UUID id);

    User createOrganizationEntityUser(UserRequest userRequest);

    Integer countByRole(Role role);

    void delete(Integer id, UpdateReason reason);

    UserProfileDTO mapUserToUserProfileDTO(User user);

    List<WorkingHoursDTO> mapWorkingHoursToDTO(List<WorkingHours> workingHours);

    Page<User> getSellsManagers(String name, Pageable pageable);

    Page<UserInfoDto> getUsersByOrganization(UUID organizationId, Pageable pageable);

    Page<UserInfoDto> getUsersBySubEntity(UUID subEntityId, Pageable pageable);

    void deleteUser(Integer id, UpdateReason request);

    Page<UpdateDetails> getDeletionDetails(Integer uuid, Pageable pageable);
}
