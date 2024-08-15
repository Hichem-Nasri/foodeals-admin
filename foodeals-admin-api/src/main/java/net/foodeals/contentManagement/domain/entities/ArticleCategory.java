package net.foodeals.contentManagement.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "blog_categories")

@Getter
@Setter
public class ArticleCategory extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String name;

    private String slug;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "categories")
    private List<Article> articles = new ArrayList<>();
}
