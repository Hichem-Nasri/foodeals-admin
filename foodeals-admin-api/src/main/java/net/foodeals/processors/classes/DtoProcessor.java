package net.foodeals.processors.classes;

import net.foodeals.processors.annotations.Processable;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.util.List;

@Component
public class DtoProcessor {

    private final LowercaseProcessor lowercaseProcessor;

    public DtoProcessor(LowercaseProcessor lowercaseProcessor) {
        this.lowercaseProcessor = lowercaseProcessor;
    }

    public void processDto(Object dto) throws IllegalAccessException {
        Field[] fields = dto.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Processable.class)) {
                field.setAccessible(true);
                Object value = field.get(dto);
                if (value instanceof String) {
                    String processed = lowercaseProcessor.process((String) value);
                    field.set(dto, processed);
                } else if (value instanceof List && !((List<?>) value).isEmpty() && ((List<?>) value).get(0) instanceof String) {
                    @SuppressWarnings("unchecked")
                    List<String> processed = lowercaseProcessor.process((List<String>) value);
                    field.set(dto, processed);
                }
            }
        }
    }
}
