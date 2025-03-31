package com.bach.RoomRentalManagementSystem.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendHtmlEmail(String fromEmail, String toEmail, String subject, String template, Map<String, String> parameters) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        ClassPathResource resource = new ClassPathResource(template);
        String htmlTemplate = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
        String htmlContent = replaceTemplateVariables(htmlTemplate, parameters);

        System.out.println("Parameters: " + parameters);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    private String replaceTemplateVariables(String template, Map<String, String> parameters) {
        String result = template;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}
