package net.foodeals.contentManagement.domain.controllers;

import net.foodeals.contentManagement.domain.Dto.response.ArticleCategoryDto;
import net.foodeals.contentManagement.domain.Dto.response.ArticleDto;
import net.foodeals.contentManagement.domain.Dto.upload.CreateArticleCategoryDto;
import net.foodeals.contentManagement.domain.Dto.upload.CreateArticleDto;
import net.foodeals.contentManagement.domain.Dto.upload.UpdateArticleCategoryDto;
import net.foodeals.contentManagement.domain.Dto.upload.UpdateArticleDto;
import net.foodeals.contentManagement.domain.entities.Article;
import net.foodeals.contentManagement.domain.entities.ArticleCategory;
import net.foodeals.contentManagement.domain.services.ArticleCategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class ArticleCategoriesController {

    private final ArticleCategoryService articleCategoryService;
    private final ModelMapper modelMapper;


    public ArticleCategoriesController(ArticleCategoryService articleCategoryService, ModelMapper modelMapper) {
        this.articleCategoryService = articleCategoryService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/ArticleCategories")
    public ResponseEntity<List<ArticleCategoryDto>> getAllCategories() {
        List<ArticleCategory> articleCategories = this.articleCategoryService.getAllArticleCategories();
        List<ArticleCategoryDto> articleCategoryDto = articleCategories.stream()
                .map(articleCategory -> modelMapper.map(articleCategory, ArticleCategoryDto.class))
                .toList();
        return new ResponseEntity<List<ArticleCategoryDto>>(articleCategoryDto, HttpStatus.OK);
    }

    @GetMapping("/ArticleCategory/{uuid}")
    public ResponseEntity<ArticleCategoryDto> getArticleCategoryByUuid(@PathVariable("uuid") String uuid) {
        ArticleCategory articleCategory = this.articleCategoryService.getArticleByUuid(uuid);
        if (articleCategory == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ArticleCategory Not Found");
        }

        ArticleCategoryDto articleCategoryDto = this.modelMapper.map(articleCategory, ArticleCategoryDto.class);
        return new ResponseEntity<ArticleCategoryDto>(articleCategoryDto, HttpStatus.OK);
    }

    @PostMapping("/ArticleCategory")
    public ResponseEntity<ArticleCategoryDto> createAnArticleCategory(@RequestBody CreateArticleCategoryDto createArticleCategoryDto) {
        ArticleCategory articleCategory = this.articleCategoryService.createAnArticleCategory(createArticleCategoryDto);
        ArticleCategoryDto articleCategoryDto = this.modelMapper.map(articleCategory, ArticleCategoryDto.class);
        return new ResponseEntity<ArticleCategoryDto>(articleCategoryDto, HttpStatus.OK);
    }

    @PutMapping("ArticleCategory/{uuid}")
    public ResponseEntity<ArticleCategoryDto> updateAnArticle(@PathVariable("uuid") String uuid, @RequestBody UpdateArticleCategoryDto updateArticleCategoryDto) {
        ArticleCategory articleCategory = this.articleCategoryService.updateAnArticleCategoryById(uuid, updateArticleCategoryDto);
        ArticleCategoryDto articleCategoryDto = this.modelMapper.map(articleCategory, ArticleCategoryDto.class);
        return new ResponseEntity<ArticleCategoryDto>(articleCategoryDto, HttpStatus.OK);
    }

    @DeleteMapping("ArticleCategory/{uuid}")
    public ResponseEntity<ArticleCategoryDto> deleteAnArticleCategory(@PathVariable("uuid") String uuid) {
        ArticleCategory articleCategory = this.articleCategoryService.deleteAnArticleCategoryByUuid(uuid);
        ArticleCategoryDto articleCategoryDto = this.modelMapper.map(articleCategory, ArticleCategoryDto.class);
        return new ResponseEntity<ArticleCategoryDto>(articleCategoryDto, HttpStatus.OK);
    }

}
