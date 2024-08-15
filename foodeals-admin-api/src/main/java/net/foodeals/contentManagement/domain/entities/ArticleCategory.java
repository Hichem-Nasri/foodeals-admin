package net.foodeals.contentManagement.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "blog_categories")

@Getter
@Setter
public class ArticleCategory extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String slug;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "categories")
    private List<Article> articles = new ArrayList<>();
}
