package com.ureca.config.Redis;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisLockUtil {

    private final StringRedisTemplate redisTemplate;

    public RedisLockUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 락 생성
    public boolean tryLock(String key, long expireTimeInMillis) {
        Boolean success =
                redisTemplate
                        .opsForValue()
                        .setIfAbsent(key, "LOCKED", Duration.ofMillis(expireTimeInMillis));
        return Boolean.TRUE.equals(success);
    }

    // 락 해제
    public void unlock(String key) {
        redisTemplate.delete(key);
    }
}
