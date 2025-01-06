package com.jwt.JwtSecurity.config.aspect;

import com.jwt.JwtSecurity.config.annotation.RequestRateLimiter;
import com.jwt.JwtSecurity.exception.LimitReachedException;
import com.jwt.JwtSecurity.service.RateLimiterService;
import com.jwt.JwtSecurity.utils.AppMessages;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class RateLimiterAspect {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Around("@annotation(requestRateLimiter)")
    public Object enforceRateLimiter(ProceedingJoinPoint joinPoint, RequestRateLimiter requestRateLimiter) throws Throwable {

        String endpoint = requestRateLimiter.endpoint();
        int limit = requestRateLimiter.limit();
        int duration = requestRateLimiter.duration();

        if (!rateLimiterService.isRateLimitExceeded(endpoint, limit, duration))
            throw new LimitReachedException(AppMessages.LIMIT_EXCEEDED);

        return joinPoint.proceed();
    }
}
