package net.foodeals.user.application.services;

import net.foodeals.user.domain.entities.Role;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    List<Role> findAll();
    
    Role findById(UUID id);

    Role findByName(String name);
}
