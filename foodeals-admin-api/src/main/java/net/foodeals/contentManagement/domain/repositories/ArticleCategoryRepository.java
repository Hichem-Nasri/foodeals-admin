package net.foodeals.contentManagement.domain.repositories;

import net.foodeals.contentManagement.domain.entities.ArticleCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, Long> {
}
