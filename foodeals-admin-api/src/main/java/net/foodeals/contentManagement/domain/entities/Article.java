package net.foodeals.contentManagement.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.foodeals.common.models.AbstractEntity;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "article")

@Getter
@Setter
public class Article extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String title;

    private String slug;

    private String content;

    @Column(name = "thumbnail_path") // relation needed
    private String thumbnailPath;

    @ManyToMany
    private List<ArticleCategory> categories;

}