package net.foodeals.user.application.services.impl;

import lombok.RequiredArgsConstructor;
import net.foodeals.user.application.services.RoleService;
import net.foodeals.user.domain.entities.Role;
import net.foodeals.user.domain.exceptions.RoleNotFoundException;
import net.foodeals.user.domain.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role findById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id));
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException(name));
    }
}
