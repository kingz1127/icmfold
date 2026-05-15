package com.example.InnerCityBackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.from}") // This matches your new YAML structure
    private String senderEmail;


    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Async
    public void sendOtpEmail(String to, String otpCode) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject("Verification Code - InnerCity Mission");

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #eee;'>" +
                    "<h2>Verify Your Email</h2>" +
                    "<p>Hello,</p>" +
                    "<p>Your one-time verification code is:</p>" +
                    "<h1 style='color: #007bff; letter-spacing: 5px;'>" + otpCode + "</h1>" +
                    "<p>This code will expire in 10 minutes.</p>" +
                    "<p>If you did not request this code, please ignore this email.</p>" +
                    "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info("Brevo: OTP successfully sent to {}", to);
        } catch (MessagingException e) {
            log.error("Brevo: Failed to send OTP email: {}", e.getMessage());
        }
    }

    @Async
    public void sendPasswordResetEmail(String to, String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject("Reset Your Password - InnerCity Mission");

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                    "<h2>Password Reset Request</h2>" +
                    "<p>You requested to reset your password. Click the button below to proceed:</p>" +
                    "<a href='" + resetLink + "' style='background-color: #28a745; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; display: inline-block;'>Reset Password</a>" +
                    "<p style='margin-top: 20px;'>If the button doesn't work, copy and paste this link:</p>" +
                    "<p>" + resetLink + "</p>" +
                    "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info("Brevo: Reset link successfully sent to {}", to);
        } catch (MessagingException e) {
            log.error("Brevo: Failed to send reset email: {}", e.getMessage());
        }
    }
}