package net.foodeals.payment.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PartnerInfoDto {
    private String name;

    private String avatarPath;
}
