package com.jwt.JwtSecurity.service;

import com.jwt.JwtSecurity.security.JwtService;
import com.jwt.JwtSecurity.utils.CacheConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class RateLimiterService {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Autowired
    JwtService jwtService;

    public Mono<Boolean> isAllowed(String key, String timestampKey, int limit, long durationInSeconds) {
        return redisTemplate.opsForValue()
                .increment(key)
                .flatMap(count -> {
                    if (count == 1) {
                        return redisTemplate.opsForValue()
                                .set(timestampKey, String.valueOf(System.currentTimeMillis()))
                                .then(redisTemplate.expire(key, Duration.ofSeconds(durationInSeconds)))
                                .then(redisTemplate.expire(timestampKey, Duration.ofSeconds(durationInSeconds)))
                                .then(Mono.just(true));
                    } else if (count > limit) {
                        return Mono.just(false);
                    } else {
                        return redisTemplate.opsForValue()
                                .get(timestampKey)
                                .map(timestamp -> {
                                    long currentTime = System.currentTimeMillis();
                                    long storedTime = Long.parseLong(timestamp);
                                    long elapsedTime = currentTime - storedTime;

                                    if (elapsedTime > durationInSeconds * 1000) {
                                        redisTemplate.opsForValue().set(key, "1").subscribe();
                                        redisTemplate.opsForValue()
                                                .set(timestampKey, String.valueOf(System.currentTimeMillis()))
                                                .subscribe();
                                        redisTemplate.expire(key, Duration.ofSeconds(durationInSeconds)).subscribe();
                                        redisTemplate.expire(timestampKey, Duration.ofSeconds(durationInSeconds)).subscribe();
                                        return true;
                                    }
                                    return true;
                                });
                    }
                })
                .flatMap(allowed -> {
                    if (allowed) {
                        return redisTemplate.expire(key, Duration.ofSeconds(durationInSeconds))
                                .then(redisTemplate.expire(timestampKey, Duration.ofSeconds(durationInSeconds)))
                                .thenReturn(true);
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    public boolean isRateLimitExceeded(String endpoint, int limit, int duration) {
        String key = jwtService.extractEmailFromToken() + CacheConstants.RATE_LIMIT_KEY_BASE + endpoint;
        String timeStampKey = key + CacheConstants.TIMESTAMP_KEY;
        boolean allowed = Boolean.TRUE.equals(isAllowed(key, timeStampKey, limit, duration).block());
        return allowed;
    }

    public Boolean isAllowedOpt(String key, String timestampKey, int limit, long durationInSeconds) {

        Long count = redisTemplate.opsForValue().increment(key).block();

        if (count == null) return false;

        if (count == 1) {
            redisTemplate.opsForValue().set(timestampKey, String.valueOf(System.currentTimeMillis()));
            redisTemplate.expire(key, Duration.ofSeconds(durationInSeconds));
            redisTemplate.expire(timestampKey, Duration.ofSeconds(durationInSeconds));
            return true;
        } else if (count > limit) {
            // Exceeded rate limit
            return false;
        } else {
            // Check the timestamp
            String storedTimestamp = redisTemplate.opsForValue().get(timestampKey).block();

            if (storedTimestamp != null) {
                long currentTime = System.currentTimeMillis();
                long storedTime = Long.parseLong(storedTimestamp);

                if (currentTime - storedTime > durationInSeconds * 1000) {
                    // Reset the counter and timestamp
                    redisTemplate.opsForValue().set(key, "1");
                    redisTemplate.opsForValue().set(timestampKey, String.valueOf(currentTime));
                    redisTemplate.expire(key, Duration.ofSeconds(durationInSeconds));
                    redisTemplate.expire(timestampKey, Duration.ofSeconds(durationInSeconds));
                    return true;
                }
            }

            return true;
        }
    }


}