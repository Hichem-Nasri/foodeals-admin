package net.foodeals.crm.application.dto.responses;

public record ProspectStatisticDto(Integer activeLeads, Long total, Integer notConverted, Integer converted) {
}
