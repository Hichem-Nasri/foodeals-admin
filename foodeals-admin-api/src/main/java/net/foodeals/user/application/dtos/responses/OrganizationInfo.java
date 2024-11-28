package net.foodeals.user.application.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.foodeals.payment.application.dto.response.PartnerInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationInfo {
    private PartnerInfoDto organization;
    private PartnerInfoDto subentity;
    private  String rayon;
    private SimpleUserDto manager;
    private String city;
    private String region;
    private  List<String> solutions;
    private String phone;
    private String email;
}
