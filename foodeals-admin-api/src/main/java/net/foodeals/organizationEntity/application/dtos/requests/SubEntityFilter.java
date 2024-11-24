package net.foodeals.organizationEntity.application.dtos.requests;

import lombok.Builder;
import lombok.Data;
import net.foodeals.organizationEntity.domain.entities.enums.SubEntityType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SubEntityFilter {
    private Instant startDate;      // Filter by start date
    private Instant endDate;        // Filter by end date
    private List<String> names;     // Filter by names
    private List<String> solutions; // Filter by solutions (if applicable)
    private UUID cityId;            // Filter by city ID
    private Integer collabId;    // Filter by a list of collaborator IDs
    private String email;           // Filter by email
    private String phone;           // Filter by phone
    private Boolean deletedAt;
    private List<SubEntityType> types;// Filter by deleted status
}
