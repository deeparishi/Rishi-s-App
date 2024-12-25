package com.jwt.JwtSecurity.service.impl;

import com.jwt.JwtSecurity.service.iservice.IMailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService implements IMailService{

    @Autowired
    JavaMailSender mailSender;

    @Override
    public void sendForgotPasswordLink(String content, String subject, String ...recipient) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        messageHelper.setSubject(subject);
        messageHelper.setTo(recipient);
        messageHelper.setFrom("deeparishia@gmail.com");
        messageHelper.setText(content, true);
        mailSender.send(mimeMessage);
    }
}
