package com.jwt.JwtSecurity.service.iservice;

import jakarta.mail.MessagingException;

public interface IMailService {

    void sendForgotPasswordLink(String email, String subject, String ...recipient) throws MessagingException;
}
