package net.foodeals.contentManagement.domain.Dto.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateArticleDto {
    private String title;

    private String content;
}
