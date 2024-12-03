package com.jwt.JwtSecurity.service.iservice;

public interface ITemplateService {

    String getForgotPasswordTemplate(String email, String recipientName,String token);
}
