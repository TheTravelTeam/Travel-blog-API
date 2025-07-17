package com.wcs.travel_blog.util;

public class SlugUtil {
    public static String slugify(String input) {
        if(input == null) return "";
        String slug= input
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");

        if (slug.length() > 60) {
            slug = slug.substring(0, 60).replaceAll("-+$", "");
        }
        return slug;
    }
}
