package com.ulysseprod.Email;

import com.ulysseprod.Entities.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;


//    private final SpringTemplateEngine templateEngine;
//@Async
//    public void sendEmail(String to, String username,
//                          EmailTemplateName emailTemplate,
//                          String confirmationUrl,
//                          String activationCode, String subject) throws MessagingException {
//            String templateName;
//            if (emailTemplate==null)
//            {
//                templateName="confirm-email"; }
//            else{
//                    templateName = emailTemplate.name(); }

//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(
//                mimeMessage,
//                MULTIPART_MODE_MIXED,
//                UTF_8.name()
//        );
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("username", username);
//        properties.put("confirmationUrl", confirmationUrl);
//        properties.put("activation_code", activationCode);
//
//        Context context = new Context();
//        context.setVariables(properties);

//        helper.setFrom("");
//        helper.setTo(to);
//        helper.setSubject(subject);
//
//        String template = templateEngine.process(templateName, context);
//
//        helper.setText(template, true);
//
//        mailSender.send(mimeMessage);
//    }

    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        String subject = "Password Reset";
        String body = "Dear User,\n\nPlease click on the following link to reset your password:\n\n"
                + "http://localhost:80802/reset-password?token=" + token + "\n\nThank you.";



        sendEmail(to, subject, body);
    }

    private void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(message);
    }

}
