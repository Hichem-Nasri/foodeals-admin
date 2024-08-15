package net.foodeals.contentManagement.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;

import java.util.List;

@Entity
@Table(name = "article")

@Getter
@Setter
public class Article extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String slug;

    private String content;

    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    @ManyToMany
    private List<ArticleCategory> categories;

}