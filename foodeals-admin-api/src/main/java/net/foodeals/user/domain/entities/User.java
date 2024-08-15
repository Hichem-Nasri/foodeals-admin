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

    @OneToOne
    private OrganizationEntity organizationEntity;

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .toList();
    }

    /**
     * Returns the username used to authenticate the user. Cannot return
     * <code>null</code>.
     *
     * @return the username (never <code>null</code>)
     */
    @Override
    public String getUsername() {
        return String.format("%s %s", name.firstName(), name.lastName());
    }
}
