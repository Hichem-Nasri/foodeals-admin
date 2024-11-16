package net.foodeals.user.application.dtos.responses;

import net.foodeals.user.domain.valueObjects.Name;

public record SimpleUserDto(
        Integer id,
        Name name,
        String avatarPath
) {}