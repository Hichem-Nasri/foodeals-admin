package net.foodeals.entity.domain.valueObject;

import jakarta.persistence.Embeddable;

@Embeddable
public record Coordinates(Float latitude, Float longitude) {
}
