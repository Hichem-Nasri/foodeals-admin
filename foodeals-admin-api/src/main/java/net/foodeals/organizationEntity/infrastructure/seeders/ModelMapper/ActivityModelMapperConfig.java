package net.foodeals.organizationEntity.infrastructure.seeders.ModelMapper;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.organizationEntity.application.dtos.responses.ActivityResponse;
import net.foodeals.organizationEntity.application.dtos.responses.ActivityResponseDto;
import net.foodeals.organizationEntity.domain.entities.Activity;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class ActivityModelMapperConfig {

    private ModelMapper modelMapper;

    @PostConstruct
    private void postConstruct() {
        this.modelMapper.addConverter(mappingContext -> {
            Activity activity = mappingContext.getSource();

            return new ActivityResponseDto(activity.getId(), activity.getName(), activity.getType());
        }, Activity.class, ActivityResponseDto.class);
    }
}
