package com.wcs.travel_blog.notification.service;

public interface MailService {

    /**
     * Sends a plain-text email and lets callers handle transport failures via {@link org.springframework.mail.MailException}.
     */
    void send(String to, String subject, String body);
}
