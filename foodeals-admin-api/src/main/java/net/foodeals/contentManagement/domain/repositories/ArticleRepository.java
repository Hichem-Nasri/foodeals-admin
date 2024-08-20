package net.foodeals.contentManagement.domain.repositories;

import net.foodeals.contentManagement.domain.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID> {
    boolean existsBySlug(String slug);
}
