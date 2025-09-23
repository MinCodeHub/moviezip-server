package com.example.moviezip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RedisTestRunner {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct  // 애플리케이션 시작 후 자동 실행
    public void testRedis() {
        redisTemplate.opsForValue().set("testKey", "testValue");
        Object value = redisTemplate.opsForValue().get("testKey");
        System.out.println("✅ Redis testKey value: " + value);
    }
}
