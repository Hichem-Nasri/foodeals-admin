package net.foodeals.organizationEntity.application.dtos.requests;

import lombok.Data;
import net.foodeals.user.domain.entities.enums.DeletionReason;

@Data
public class DeleteOrganizationRequest {
    private DeletionReason reason;
    private String details;

    // Getters and setters
    public DeletionReason getReason() {
        return reason;
    }

    public void setReason(DeletionReason reason) {
        this.reason = reason;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}