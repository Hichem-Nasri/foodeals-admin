package net.foodeals.user.application.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.String;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkingHoursDTO {
    private String dayOfWeek;
    private String morningStart;
    private String morningEnd;
    private String afternoonStart;
    private String afternoonEnd;

    // Getters and setters
}
