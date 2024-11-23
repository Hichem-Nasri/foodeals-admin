package net.foodeals.organizationEntity.application.dtos.responses;

import net.foodeals.user.domain.entities.enums.ReasonType;

import java.time.Instant;

public class DeletionDetailsDTO {
    private ReasonType reason;
    private String details;
    private Instant deletedAt;

    public DeletionDetailsDTO(ReasonType reason, String details, Instant deletedAt) {
        this.reason = reason;
        this.details = details;
        this.deletedAt = deletedAt;
    }

    // Getters
    public ReasonType getReason() {
        return reason;
    }

    public String getDetails() {
        return details;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}