package net.foodeals.contentManagement.domain.repositories;

import net.foodeals.contentManagement.domain.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
