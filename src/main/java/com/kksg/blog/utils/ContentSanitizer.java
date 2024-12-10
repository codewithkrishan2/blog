package com.kksg.blog.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class ContentSanitizer {

    // Use JSoup to clean content and remove harmful tags
    public String sanitizeContent(String input) {
        // Using a Safelist to allow only specific safe HTML tags
        // You can customize the allowed tags and attributes.
        return Jsoup.clean(input, Safelist.basic()); // Safelist.basic() allows only basic tags like <b>, <i>, <p>, etc.
    }
}