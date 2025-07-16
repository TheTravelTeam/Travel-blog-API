package com.wcs.travel_blog.util;

public class SlugUtil {
    public static String slugify(String input) {
        if(input == null) return "";
        return input
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
    }
}
