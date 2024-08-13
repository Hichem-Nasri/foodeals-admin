package net.foodeals.user.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.user.domain.valueObjects.AuthenticationTokenDetails;

/**
 * Account
 */
@Entity
@Table(name = "accounts")

@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    private String type;

    private String provider;

    @Column(name = "provider_account_id")
    private String providerAccountId;

    @Embedded
    private AuthenticationTokenDetails tokenDetails;

    private String scope;

    @Column(name = "id_token")
    private String idToken;

    @Column(name = "session_state")
    private String sessionState;
}
