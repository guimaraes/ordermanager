package br.com.ambevtech.ordermanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private static final long EXPIRATION_TIME = 3600; // 1 hora

    public void saveToCache(String key, String value) {
        redisTemplate.opsForValue().set(key, value, EXPIRATION_TIME, TimeUnit.SECONDS);
    }

    public Optional<String> getFromCache(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }
}

