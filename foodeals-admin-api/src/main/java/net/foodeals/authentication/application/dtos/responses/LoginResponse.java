package net.foodeals.authentication.application.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.foodeals.user.domain.valueObjects.Name;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Name name;
    private String role;
    private String avatarPath;
    private Integer id;
    private AuthenticationResponse token;
}
