package net.foodeals.organizationEntity.application.dtos.responses;

import lombok.Data;

@Data
public class OrganizationEntityDto {
    private String createdAt;

    private String avatarPath;

    private String name;

    private String manager;

    private String contractStatus;

    private String email;

    private String phone;
}
