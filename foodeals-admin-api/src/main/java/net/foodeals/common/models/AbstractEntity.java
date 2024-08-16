package net.foodeals.common.models;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

/**
 * AbstractEntity
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity<T> implements Serializable {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Getter
    @Setter
    private Instant createdAt = Instant.now();

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

    public void markDeleted() {
        this.deletedAt = Instant.now();
    }
}
