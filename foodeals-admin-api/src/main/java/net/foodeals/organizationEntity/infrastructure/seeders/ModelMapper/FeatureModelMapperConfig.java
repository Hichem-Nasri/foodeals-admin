package net.foodeals.organizationEntity.infrastructure.seeders.ModelMapper;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.foodeals.organizationEntity.application.dtos.responses.FeatureResponse;
import net.foodeals.organizationEntity.domain.entities.Features;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class FeatureModelMapperConfig {

    private ModelMapper modelMapper;
    @PostConstruct
    @Transactional
    private void postConstruct() {
        this.modelMapper.addConverter(mappingContext -> {
            Features feature = mappingContext.getSource();

            return new FeatureResponse(feature.getId(), feature.getName());
        }, Features.class, FeatureResponse.class);
    }

}
