package com.wcs.travel_blog.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:}")
    private String defaultSender;

    @Override
    /**
     * Builds a MimeMessage with UTF-8 encoding, applies the optional configured sender, and delegates to JavaMail.
     */
    public void send(String to, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);
            if (StringUtils.hasText(defaultSender)) {
                helper.setFrom(defaultSender);
            }
        } catch (MessagingException exception) {
            throw new MailPreparationException("Unable to prepare email", exception);
        }
        mailSender.send(message);
    }
}
