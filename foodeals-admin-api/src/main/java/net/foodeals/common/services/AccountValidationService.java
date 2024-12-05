package net.foodeals.common.services;

import jakarta.transaction.Transactional;
import net.foodeals.user.domain.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountValidationService {

    private final EmailService emailService;
    private final ResourceLoader resourceLoader;

    @Autowired
    public AccountValidationService(
            EmailService emailService,
            ResourceLoader resourceLoader
    ) {
        this.emailService = emailService;
        this.resourceLoader = resourceLoader;
    }

    @Transactional
    public void validateManagerAccount(User manager, String pass) {
        try {
            String emailTemplate = new String(Files.readAllBytes(Paths.get("resources/email.html")));


            String processedEmailTemplate = emailTemplate
                    .replace("${user_email}", manager.getEmail())
                    .replace("${user_password}", pass);

            String subject = "Validation de Votre Compte Foodeals";
            String receiver = manager.getEmail();

            this.emailService.sendEmail(receiver, subject, processedEmailTemplate);

        } catch (IOException e) {
            log.error("Failed to send validation email for manager: {}",
                    manager.getEmail(), e);
            throw new EmailValidationException(
                    "Unable to send validation email", e
            );
        }
    }

    public static class EmailValidationException extends RuntimeException {
        public EmailValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}