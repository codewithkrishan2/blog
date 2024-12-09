package com.kksg.blog.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SlugUtil {

	// Method to generate a URL-friendly slug from the post title
    public static String generateSlug(String title) {
        if (title == null) {
            return null;
        }

        // Normalize the title to remove special characters and accents
        String slug = Normalizer.normalize(title, Normalizer.Form.NFD);
        slug = Pattern.compile("[^\\p{ASCII}]").matcher(slug).replaceAll(""); // Remove non-ASCII characters

        // Convert to lowercase and replace spaces with hyphens
        slug = slug.trim().toLowerCase().replaceAll("[^a-z0-9\\s-]", "") // Remove unwanted characters
                 .replaceAll("\\s+", "-"); // Replace spaces with hyphens

        return slug;
    }
	
    public static String generateKeywords(String content) {
        // Simple example: take the first 5 words from the content as keywords
        String[] words = content.split("\\s+");
        StringBuilder keywords = new StringBuilder();
        for (int i = 0; i < Math.min(words.length, 5); i++) {
            keywords.append(words[i]).append(",");
        }
        return keywords.toString().trim();
    }

    
}
