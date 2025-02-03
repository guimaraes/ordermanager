package br.com.ambevtech.ordermanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @InjectMocks
    private RedisService redisService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private static final String CACHE_KEY = "testKey";
    private static final String CACHE_VALUE = "testValue";

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void saveToCache_ShouldStoreValueInRedis() {
        doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        redisService.saveToCache(CACHE_KEY, CACHE_VALUE);

        verify(valueOperations, times(1)).set(eq(CACHE_KEY), eq(CACHE_VALUE), eq(3600L), eq(TimeUnit.SECONDS));
    }

    @Test
    void getFromCache_ShouldReturnCachedValue() {
        when(valueOperations.get(CACHE_KEY)).thenReturn(CACHE_VALUE);

        Optional<String> result = redisService.getFromCache(CACHE_KEY);

        assertTrue(result.isPresent());
        assertEquals(CACHE_VALUE, result.get());
        verify(valueOperations, times(1)).get(CACHE_KEY);
    }

    @Test
    void getFromCache_ShouldReturnEmptyIfKeyNotFound() {
        when(valueOperations.get(CACHE_KEY)).thenReturn(null);

        Optional<String> result = redisService.getFromCache(CACHE_KEY);

        assertFalse(result.isPresent());
        verify(valueOperations, times(1)).get(CACHE_KEY);
    }
}
