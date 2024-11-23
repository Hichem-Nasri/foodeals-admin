package net.foodeals.organizationEntity.application.dtos.requests;

import lombok.Data;
import net.foodeals.user.domain.entities.enums.ReasonType;

@Data
public class DeleteOrganizationRequest {
    private ReasonType reason;
    private String details;

    // Getters and setters
    public ReasonType getReason() {
        return reason;
    }

    public void setReason(ReasonType reason) {
        this.reason = reason;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}