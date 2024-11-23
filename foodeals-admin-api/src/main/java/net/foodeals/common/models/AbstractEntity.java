package net.foodeals.common.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.entities.enums.ActionType;
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

    public abstract T getId();


    public void markDeleted(ActionType action) {
        if (action.equals(ActionType.ARCHIVE)) {
            this.setDeletedAt(Instant.now());
        } else if (action.equals(ActionType.DE_ARCHIVE)) {
            this.setDeletedAt(null);
        }
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}