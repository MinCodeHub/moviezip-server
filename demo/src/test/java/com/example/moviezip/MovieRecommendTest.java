package com.example.moviezip;

import com.example.moviezip.service.recommend.MovieCacheRecommenderService;
import com.mongodb.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MovieRecommendTest {

    @Autowired
    private MovieCacheRecommenderService recommenderService;

    @Autowired private RedisTemplate<String, Object> redisTemplate;


    private String key(int uid){ return "recommend:" + uid; }

    @Before
    public void clearCache() {
        int uid = 152;
        redisTemplate.delete(key(uid)); // 반드시 콜드 스타트로
    }

    @Test
    public void testRecommendMoviesSpeedComparison() {
        int testUserId = 152;
        redisTemplate.delete("recommend:" + testUserId);
        // 1. 캐시 미적용 (첫 호출 → 추천 계산 수행)
        long start1 = System.currentTimeMillis();
        List<String> result1 = recommenderService.getRecommendations(testUserId);
        long end1 = System.currentTimeMillis();
        System.out.println("추천 계산 소요 시간 (캐시 없음): " + (end1 - start1) + "ms");

        // 2️. 캐시 적용 (두 번째 호출 → Redis에서 가져옴)
        long start2 = System.currentTimeMillis();
        List<String> result2 = recommenderService.getRecommendations(testUserId);
        long end2 = System.currentTimeMillis();
        System.out.println("추천 조회 소요 시간 (캐시 적용): " + (end2 - start2) + "ms");

        // 검증
        Assertions.assertFalse(result1.isEmpty());
        Assertions.assertFalse(result2.isEmpty());
    }
}
