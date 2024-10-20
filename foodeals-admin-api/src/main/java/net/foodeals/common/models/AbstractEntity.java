package net.foodeals.common.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.user.domain.entities.enums.DeletionReason;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity<T> implements Serializable {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Getter
    private final Instant createdAt = Instant.now();

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Getter
    @Setter
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    @Getter
    @Setter
    private Instant deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "deletion_reason")
    @Getter
    @Setter
    private DeletionReason deletionReason;

    @Column(name = "deletion_details", length = 2000)
    @Getter
    @Setter
    private String deletionDetails;

    public abstract T getId();


    public void markDeleted(DeletionReason reason, String details) {
        this.deletedAt = Instant.now();
        this.deletionReason = reason;
        this.deletionDetails = details;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}