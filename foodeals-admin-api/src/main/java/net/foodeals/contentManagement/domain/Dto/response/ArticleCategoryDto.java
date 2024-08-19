package net.foodeals.contentManagement.domain.Dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ArticleCategoryDto {

    private String id;

    private String name;

    private String Slug;

    private List<ArticleDto> articleDto;
}
