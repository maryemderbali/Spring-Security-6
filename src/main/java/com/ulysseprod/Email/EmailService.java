package com.ulysseprod.Email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.util.HashMap;
import java.util.Map;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    @Autowired
    private final JavaMailSender mailSender;



    @Autowired
    private final SpringTemplateEngine templateEngine;
    @Async
    public void sendEmail(String to, String username,
                          EmailTemplateName emailTemplate,
                          String confirmationUrl,
                          String activationCode, String subject) throws MessagingException {
            String templateName;
            if (emailTemplate==null)
            {
                templateName="confirm-email"; }
            else{
                    templateName = emailTemplate.name(); }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MULTIPART_MODE_MIXED,
                UTF_8.name() );

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code", activationCode);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom("contact.prod@ulysse.media");
        helper.setTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName, context);

        helper.setText(template, true);

        mailSender.send(message);
    }
    public void sendPasswordResetEmail(String to, String username, String token) throws MessagingException {
        String subject = "Password Reset Request";
        String resetUrl = "http://localhost:8082/Reset-Password?token=" + token;

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("resetUrl", resetUrl);

        Context context = new Context();
        context.setVariables(properties);

        String templateName = EmailTemplateName.RESET_PASSWORD.name();
        String htmlBody = templateEngine.process(templateName, context);

        String textBody = "Dear " + username + ",\n\n"
                + "We received a request to reset your password. Please click on the following link to create a new password:\n"
                + resetUrl + "\n\n"
                + "Thank you.";

        sendEmailpass(to, subject, htmlBody, textBody);
    }

    @Async
    public void sendEmailpass(String to, String subject, String htmlBody, String textBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MULTIPART_MODE_MIXED, UTF_8.name());

        helper.setFrom("contact.prod@ulysse.media");
        helper.setTo(to);
        helper.setSubject(subject);

        helper.setText(textBody, false);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }

}
