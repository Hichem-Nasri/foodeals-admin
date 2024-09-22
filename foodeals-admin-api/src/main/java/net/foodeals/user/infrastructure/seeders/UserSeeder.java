package net.foodeals.user.infrastructure.seeders;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.location.domain.entities.Address;
import net.foodeals.location.domain.entities.City;
import net.foodeals.location.domain.entities.Country;
import net.foodeals.location.domain.entities.State;
import net.foodeals.location.domain.repositories.AddressRepository;
import net.foodeals.location.domain.repositories.CityRepository;
import net.foodeals.location.domain.repositories.CountryRepository;
import net.foodeals.location.domain.repositories.StateRepository;
import net.foodeals.order.domain.repositories.OrderRepository;
import net.foodeals.user.domain.entities.Account;
import net.foodeals.user.domain.entities.Role;
import net.foodeals.user.domain.entities.User;
import net.foodeals.user.domain.entities.UserStatus;
import net.foodeals.user.domain.repositories.AccountRepository;
import net.foodeals.user.domain.repositories.RoleRepository;
import net.foodeals.user.domain.repositories.UserRepository;
import net.foodeals.user.domain.valueObjects.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Random;

@Order(8)
@Component
public class UserSeeder {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AddressRepository addressRepository;
    private final CityRepository cityRepository;
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;

    public UserSeeder(UserRepository userRepository, RoleRepository roleRepository, AddressRepository addressRepository, CityRepository cityRepository, StateRepository stateRepository, CountryRepository countryRepository, AccountRepository accountRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.addressRepository = addressRepository;
        this.cityRepository = cityRepository;
        this.accountRepository = accountRepository;
        this.orderRepository = orderRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void UserSeeder() {
        if (userRepository.count() == 0) {
            User user = this.createNewClient();
            this.userRepository.saveAndFlush(user);
        }
    }

    public User createNewClient() {
        Account account = new Account();
        net.foodeals.order.domain.entities.Order order = new net.foodeals.order.domain.entities.Order();

        account.setProvider("fb");
        Address address = new Address();
        address.setAddress("123");
        City city = this.cityRepository.findByName("Casablanca");
        address.setCity(city);
        Role i = this.roleRepository.findByName("CLIENT").orElse(null);
        this.accountRepository.saveAndFlush(account);
        this.orderRepository.saveAndFlush(order);
        this.addressRepository.saveAndFlush(address);

        Name name = new Name("test ", "test");
        User user = new User();
        user.setName(name);
        user.setRole(i);
        user.setEmail("e@gmail.com");
        user.setPhone("061");
        user.setPassword("12345");
        user.setIsEmailVerified(true);
        user.setAccount(account);
        user.setStatus(UserStatus.ACTIVE);
        user.getOrders().add(order);
        order.setClient(user);
        user.setAddress(address);
        return user;
    }
}
