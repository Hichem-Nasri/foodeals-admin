package net.foodeals.user.application.dtos.responses;

import lombok.Data;
import net.foodeals.user.domain.entities.UserStatus;

@Data
public class ClientDto {
    private String email;

    private String accountProvider;

    private String phoneNumber;

    private UserStatus accountStatus;

    private Integer numberOfCommands;

    private boolean isAccountVerified;

    private ClientAddressDto clientAddressDto;
}
