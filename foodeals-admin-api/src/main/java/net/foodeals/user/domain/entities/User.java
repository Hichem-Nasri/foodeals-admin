package net.foodeals.user.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.contract.domain.entities.UserContract;
import net.foodeals.delivery.domain.entities.Delivery;
import net.foodeals.notification.domain.entity.Notification;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.organizationEntity.domain.entities.SubEntity;
import net.foodeals.user.domain.valueObjects.Name;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * User
 */
@Entity
@Table(name = "users")

@Getter
public class User extends AbstractEntity<UUID> implements UserDetails {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Embedded
    private Name name;

    @Column(unique = true)
    private String email;

    private String phone;

    private String password;

    @Column(name = "is_email_verified")
    private Boolean isEmailVerified;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Role role;

    @ManyToOne
    private OrganizationEntity organizationEntity;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> Accounts;

    @ManyToOne
    private SubEntity subEntity;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserActivities> userActivities;

    @OneToMany(mappedBy = "deliveryBoy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Delivery> deliveries;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserContract> userContracts;

    public User(Name name, String email, String phone, String password, Boolean isEmailVerified, Role role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.isEmailVerified = isEmailVerified;
        this.role = role;
    }

    User() {

    }

    public static User create(Name name, String email, String phone, String password,
                              Boolean isEmailVerified, Role role) {
        return new User(
                name,
                email,
                phone,
                password,
                isEmailVerified,
                role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<SimpleGrantedAuthority> authorities = role.getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .toList();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public User setName(Name name) {
        this.name = name;
        return this;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public User setIsEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
        return this;
    }

    public User setRole(Role role) {
        this.role = role;
        return this;
    }

}
