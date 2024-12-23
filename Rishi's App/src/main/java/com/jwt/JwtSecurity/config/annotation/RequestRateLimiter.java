package com.jwt.JwtSecurity.config.annotation;

import com.jwt.JwtSecurity.utils.CacheConstants;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestRateLimiter {

    String endpoint();

    int limit() default CacheConstants.MAX_REQUESTS_PER_INTERVAL;

    int duration() default CacheConstants.RATE_LIMIT_INTERVAL_SECONDS;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
