package com.jwt.JwtSecurity.service.impl;

import com.jwt.JwtSecurity.service.iservice.ITemplateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class TemplateServiceImpl implements ITemplateService {

    private final TemplateEngine TEMPLATE_ENGINE;

    @Value("${my.app.password.restLink}")
    String passwordRestLink;

    public TemplateServiceImpl(TemplateEngine templateEngine) {
        this.TEMPLATE_ENGINE = templateEngine;
    }

    @Override
    public String getForgotPasswordTemplate(String email, String recipientName, String token) {

        Context context = new Context();
        context.setVariable("to", recipientName);
        context.setVariable("recepient", recipientName);
        context.setVariable("resetLink", passwordRestLink.concat(token));
        return TEMPLATE_ENGINE.process("reset-mail", context);
    }

}