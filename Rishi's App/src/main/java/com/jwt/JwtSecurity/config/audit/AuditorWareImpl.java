package com.jwt.JwtSecurity.config.audit;

import com.jwt.JwtSecurity.model.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorWareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            User user = (User) authentication.getPrincipal();
            return Optional.of(user.getId().toString());
        }

        return Optional.empty();
    }
}
