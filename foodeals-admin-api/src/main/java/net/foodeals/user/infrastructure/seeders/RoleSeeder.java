package net.foodeals.user.infrastructure.seeders;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import net.foodeals.common.annotations.Seeder;
import net.foodeals.user.domain.entities.Role;
import net.foodeals.user.domain.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.UUID;

@Seeder
@Order(7)
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final EntityManager entityManager;

    @Override
    public void run(String... args) throws Exception {
        List<Role> roles = List.of(
                Role.create(UUID.fromString("d7d7a9c5-b153-4526-ac16-05f19bf97270"), "ADMIN"),
                Role.create(UUID.randomUUID(), "SUPER_ADMIN"),
                Role.create(UUID.randomUUID(), "MANAGER"),
                Role.create(UUID.randomUUID(), "SALES_MANAGER"),
                Role.create(UUID.randomUUID(), "CLIENT"),
                Role.create(UUID.randomUUID(), "DELIVERY_MAN"),
                Role.create(UUID.randomUUID(), "LEAD")
        );

        for (Role role : roles) {
            if (!roleRepository.existsByName(role.getName())) {
                roleRepository.save(role);
            }
            System.out.println("Role " + role.getName() + " " + role.getId());
        }
    }
}
