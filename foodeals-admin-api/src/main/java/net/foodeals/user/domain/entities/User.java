package net.foodeals.user.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import net.foodeals.organizationEntity.domain.entities.OrganizationEntity;
import net.foodeals.user.domain.valueObjects.Name;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * User
 */
@Entity
@Table(name = "users")

@Getter
@Setter
public class User extends AbstractEntity<Integer> implements UserDetails {

    @Id
    @GeneratedValue
    private Integer id;

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

    @OneToOne(mappedBy = "user")
    private OrganizationEntity organizationEntity;

    public User(Name name, String email, String phone, String password, Boolean isEmailVerified, Role role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.isEmailVerified = isEmailVerified;
        this.role = role;
    }

    public User() {

    }

    public static User create(String firstName, String lastName, String email, String phone, String password, Boolean isEmailVerified, Role role) {
        return new User(
                new Name(firstName, lastName),
                email,
                phone,
                password,
                isEmailVerified,
                role
        );
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
        return String.format("%s %s", name.firstName(), name.lastName());
    }
}
