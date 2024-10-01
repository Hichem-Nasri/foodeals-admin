package net.foodeals.organizationEntity.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.foodeals.organizationEntity.domain.entities.enums.EntityType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreateAssociationDto(@NotBlank String companyName, @NotNull List<String> activities,
                                   @NotNull EntityContactDto manager1, @NotNull EntityContactDto manager2, @NotNull EntityAddressDto associationAddress,
                                   @NotNull EntityType entityType, Integer numberOfPoints, @NotNull List<String> solutions, @NotBlank String pv) {
}