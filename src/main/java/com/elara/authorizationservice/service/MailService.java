package com.elara.authorizationservice.service;

import com.elara.authorizationservice.dto.request.NotificationRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class MailService {

    @Autowired
    JavaMailSender emailSender;

    public void sendNotification(NotificationRequest notification) {
        if (StringUtils.hasText(notification.getAttachment())) {
            sendMailWithAttachment(notification);
        } else {
            sendMail(notification);
        }
    }

    private void sendMail(NotificationRequest notification) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            message.setRecipients(MimeMessage.RecipientType.TO, notification.getRecipientEmail());
            message.setFrom(new InternetAddress(notification.getSenderEmail()));
            message.setSubject(notification.getSubject());
            message.setContent(notification.getHtml() != null ? notification.getHtml() : notification.getMessage(), "text/html; charset=utf-8");
            emailSender.send(message);
        } catch (MessagingException e) {
            log.error("error sending mail: ", e);
        }
    }

    private void sendMailWithAttachment(NotificationRequest notification) {
        // Creating a Mime Message
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            // Setting multipart as true for attachment to be send
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(notification.getSenderEmail());
            mimeMessageHelper.setTo(notification.getRecipientEmail());
            mimeMessageHelper.setSubject(notification.getSubject());
            mimeMessageHelper.setText(notification.getHtml(), true);

            // Adding the file attachment
            FileSystemResource file = new FileSystemResource(new File(notification.getAttachment()));
            mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);

            // Sending the email with attachment
            emailSender.send(mimeMessage);
        }

        // Catch block to handle the MessagingException
        catch (MessagingException e) {

            // Display message when exception is occurred
            log.error("Error while sending email: ", e);
        }
    }

}