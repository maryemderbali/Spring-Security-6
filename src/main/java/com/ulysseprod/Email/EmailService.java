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
                UTF_8.name()
        );
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

    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        String subject = "Password Reset";
        String body = "Dear User,Please click on the following link to reset your password:"
                + "http://localhost:8082/Reset-Password?token=" + token + "Thank you.";



        sendEmailpass(to, subject, body);
    }
    @Async

    public void sendEmailpass(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MULTIPART_MODE_MIXED,
                UTF_8.name());
        helper.setFrom("contact.prod@ulysse.media");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(message);
    }

}
