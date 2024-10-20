package net.foodeals.organizationEntity.application.dtos.responses;

import net.foodeals.user.domain.entities.enums.DeletionReason;

import java.time.Instant;

public class DeletionDetailsDTO {
    private DeletionReason reason;
    private String details;
    private Instant deletedAt;

    public DeletionDetailsDTO(DeletionReason reason, String details, Instant deletedAt) {
        this.reason = reason;
        this.details = details;
        this.deletedAt = deletedAt;
    }

    // Getters
    public DeletionReason getReason() {
        return reason;
    }

    public String getDetails() {
        return details;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}