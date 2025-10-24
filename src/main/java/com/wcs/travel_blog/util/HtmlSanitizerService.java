package com.wcs.travel_blog.util;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Service;

@Service
public class HtmlSanitizerService {

    private final PolicyFactory policyFactory = Sanitizers.FORMATTING
            .and(Sanitizers.LINKS)
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.IMAGES);

    public String sanitize(String htmlContent) {
        if (htmlContent == null) return null;
        return policyFactory.sanitize(htmlContent);
    }
}
