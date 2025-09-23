package com.example.moviezip;

import com.example.moviezip.service.UserService;
import com.example.moviezip.service.recommend.MovieCacheRecommenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MovieCacheRecommenderServiceTest {

    @InjectMocks
    private MovieCacheRecommenderService recommenderService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }


    @Test
    void testTrainAndCacheRecommendations() {
        // given
        List<Long> mockUserIds = Arrays.asList(1L, 2L);
        when(userService.getAllUserIds()).thenReturn(mockUserIds);

        // 실제 recommendMovies를 실행하면 Spark가 돌아버리니 "Spy"로 가짜 리턴
        try (MockedStatic<MovieCacheRecommenderService> mockedStatic = mockStatic(MovieCacheRecommenderService.class)) {
            mockedStatic.when(() -> MovieCacheRecommenderService.recommendMovies(1)).thenReturn(List.of("영화1"));
            mockedStatic.when(() -> MovieCacheRecommenderService.recommendMovies(2)).thenReturn(List.of("영화2"));

            // when
            recommenderService.trainAndCacheRecommendations();

            // then
            verify(userService).getAllUserIds();
            verify(redisTemplate.opsForValue(), times(2)).set(startsWith("recommend:"), any(), eq(1L), eq(TimeUnit.DAYS));
        }
    }
}