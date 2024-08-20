package net.foodeals.contentManagement.Utils;

import net.foodeals.contentManagement.domain.entities.repositories.ArticleCategoryRepository;
import net.foodeals.contentManagement.domain.entities.repositories.ArticleRepository;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtil {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    public static String makeUniqueSlugForCategory(String slug, ArticleCategoryRepository articleCategoryRepository) {
        String uniqueSlug = slug;
        int counter = 1;
        while (articleCategoryRepository.existsBySlug(uniqueSlug)) {
            uniqueSlug = slug + "-" + counter++;
        }
        return uniqueSlug;
    }

    public static String makeUniqueSlugForArticle(String slug, ArticleRepository articleRepository) {
        String uniqueSlug = slug;
        int counter = 1;
        while (articleRepository.existsBySlug(uniqueSlug)) {
            uniqueSlug = slug + "-" + counter++;
        }
        return uniqueSlug;
    }
}

