package net.foodeals.contentManagement.domain.services;

import net.foodeals.contentManagement.domain.Dto.upload.CreateArticleCategoryDto;
import net.foodeals.contentManagement.domain.Dto.upload.UpdateArticleCategoryDto;
import net.foodeals.contentManagement.domain.Utils.SlugUtil;
import net.foodeals.contentManagement.domain.entities.Article;
import net.foodeals.contentManagement.domain.entities.ArticleCategory;
import net.foodeals.contentManagement.domain.repositories.ArticleCategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ArticleCategoryService {

    private final ArticleCategoryRepository articleCategoryRepository;

    public ArticleCategoryService(ArticleCategoryRepository articleCategoryRepository) {
        this.articleCategoryRepository = articleCategoryRepository;
    }

    public List<ArticleCategory> getAllArticleCategories() {
        return this.articleCategoryRepository.findAll();
    }

    public ArticleCategory getArticleByUuid(String uuid) {
        UUID uuidObj = UUID.fromString(uuid);
        ArticleCategory articleCategory = this.articleCategoryRepository.findById(uuidObj).orElse(null);

        if (articleCategory == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article Category not found");
        }

        return articleCategory;
    }

    public ArticleCategory createAnArticleCategory(CreateArticleCategoryDto createArticleCategoryDto) {
        ArticleCategory articleCategory = ArticleCategory.builder().name(createArticleCategoryDto.getName()).build();

        String generatedSlug = SlugUtil.toSlug(createArticleCategoryDto.getName());
        articleCategory.setSlug(SlugUtil.makeUniqueSlugForCategory(generatedSlug, this.articleCategoryRepository));

        return this.articleCategoryRepository.save(articleCategory);
    }

    public ArticleCategory updateAnArticleCategoryById(String uuid, UpdateArticleCategoryDto updateArticleCategoryDto) {
        UUID uuidObj = UUID.fromString(uuid);
        ArticleCategory articleCategory = this.articleCategoryRepository.findById(uuidObj).orElse(null);
        if (articleCategory == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "article Category not found");
        }

        if (updateArticleCategoryDto.getName().length() != 0) {
            articleCategory.setName(updateArticleCategoryDto.getName());
            String generatedSlug = SlugUtil.toSlug(updateArticleCategoryDto.getName());
            articleCategory.setSlug(SlugUtil.makeUniqueSlugForCategory(generatedSlug, this.articleCategoryRepository));
        }

        return this.articleCategoryRepository.save(articleCategory);
    }

    public ArticleCategory deleteAnArticleCategoryByUuid(String uuid) {
        UUID uuidObj = UUID.fromString(uuid);
        ArticleCategory articleCategory = this.articleCategoryRepository.findById(uuidObj).orElse(null);
        if (articleCategory == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "article Category not found");
        }

        this.articleCategoryRepository.delete(articleCategory);
        return articleCategory;
    }
}
