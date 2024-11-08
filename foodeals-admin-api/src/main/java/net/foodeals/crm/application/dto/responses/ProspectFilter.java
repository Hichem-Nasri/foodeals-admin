package net.foodeals.crm.application.dto.responses;

import lombok.Builder;
import lombok.Data;
import net.foodeals.crm.domain.entities.enums.ProspectStatus;
import net.foodeals.crm.domain.entities.enums.ProspectType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProspectFilter {
    private Instant startDate;
    private Instant endDate;
    private List<String> names;
    private List<String> categories;
    private UUID cityId;
    private UUID countryId;
    private Integer creatorId;
    private Integer leadId;
    private String email;
    private String phone;
    private List<ProspectStatus> statuses;
    private List<ProspectType> types;
}