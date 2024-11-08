package net.foodeals.crm.application.dto.responses;

public record ProspectStatisticDto(Long activeLeads, Long total, Long notConverted, Long converted) {
}
