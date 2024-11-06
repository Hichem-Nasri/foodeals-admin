package net.foodeals.user.application.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.foodeals.user.domain.entities.UserStatus;
import net.foodeals.user.domain.entities.enums.Gender;
import net.foodeals.user.domain.valueObjects.Name;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class UserProfileDTO {
    private Integer id;
    private Name name;
    private String avatarPath;
    private String email;
    private String phone;
    private String role;
    private String organization;
    private UserStatus status;
    private Gender gender;
    private String nationalId;
    private String nationality;
    private List<WorkingHoursDTO> workingHours;
}
