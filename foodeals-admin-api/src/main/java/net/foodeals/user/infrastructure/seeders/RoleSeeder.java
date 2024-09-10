package net.foodeals.user.infrastructure.seeders;

import lombok.RequiredArgsConstructor;
import net.foodeals.common.annotations.Seeder;
import net.foodeals.user.domain.entities.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.UUID;

@Seeder
@Order(5)
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            roleRepository.saveAll(
                    List.of(
                            Role.create(UUID.fromString("d7d7a9c5-b153-4526-ac16-05f19bf97270"), "ADMIN"),
                            Role.create(UUID.randomUUID(), "SUPER_ADMIN"),
                            Role.create(UUID.randomUUID(), "ASSOCIATION"),
                            Role.create(UUID.randomUUID(), "PARTNER"),
                            Role.create(UUID.randomUUID(), "SALES_MANAGER"),
                            Role.create(UUID.randomUUID(), "USER"),
                            Role.create(UUID.randomUUID(), "CLIENT")
                    )
            );
            System.out.println("roles seeded");
        }
    }
}
