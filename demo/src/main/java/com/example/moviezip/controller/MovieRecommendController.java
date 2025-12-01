package com.example.moviezip.controller;

import com.example.moviezip.domain.Movie;
import com.example.moviezip.service.MovieImpl;
import com.example.moviezip.service.recommend.MovieCacheRecommenderService;
import com.example.moviezip.service.recommend.MovieRecommenderService;
import com.example.moviezip.util.jwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;



@RestController
@RequestMapping("/")
public class MovieRecommendController {
    private final MovieCacheRecommenderService movieCacheRecommenderService;
    private final MovieRecommenderService recommenderService;
    private final MovieImpl movieService;

    private List<String> recommendedMovies = new ArrayList<>();
    private long lastRecommendationTime = 0;

    @Autowired
    private jwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public MovieRecommendController(
            MovieCacheRecommenderService movieCacheRecommenderService,
            MovieRecommenderService recommenderService,
            MovieImpl movieService) {
        this.movieCacheRecommenderService = movieCacheRecommenderService;
        this.recommenderService = recommenderService;
        this.movieService = movieService;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/main/recommend")
    public List<Movie> recommendMovies(@RequestParam int userId, @RequestHeader("Authorization") String token) throws Exception {


        long totalStart = System.currentTimeMillis();
        long jwtStart = System.currentTimeMillis();
        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        //Long userIdFromToken = jwtUtil.extractUserId(jwt); // jwtUtil은 JWT 유틸리티 클래스

        long jwtEnd = System.currentTimeMillis();
        System.out.println("JWT 파싱 시간: " + (jwtEnd - jwtStart) + " ms");

        long recommendStart = System.currentTimeMillis();

        //System.out.println("아이디아디아:::: "+ userIdFromToken);
        long currentTime = System.currentTimeMillis();
        // 하루가 지났는지 확인 (24시간 = 86400000 milliseconds)
        if (currentTime - lastRecommendationTime > 86400000) {
            // MovieRecommender 실행
            recommendedMovies = MovieRecommenderService.recommendMovies(userId);
            lastRecommendationTime = currentTime; // 마지막 추천 시간 업데이트
        }

        long recommendEnd = System.currentTimeMillis();
        System.out.println("추천 알고리즘 실행 시간: " + (recommendEnd - recommendStart) + " ms");

        if (recommendedMovies.isEmpty()) {
            System.out.println("추천 영화가 없습니다.");
            return new ArrayList<>(); // 비어 있는 경우 빈 리스트 반환
        }
        // MovieRecommender 실행
        //List<String> recommendations = recommenderService.recommendMovies(userId); // 수정된 부분
        //if (recommendations.isEmpty()) {
        //    System.out.println("없다");
        //}
        long movieFetchStart = System.currentTimeMillis();
        List<Movie> movieList = new ArrayList<>();
        for (String str : recommendedMovies) {
            System.out.println("값" + str);

            String[] parts = str.split(", ");
            String title = parts[0].split(": ")[1].replace("\"", "");
            System.out.println(title);

            Movie movie = movieService.getMovieTitle(title);
            if (movie != null) {
                movieList.add(movie);
            } else {
                System.out.println("영화 정보를 찾을 수 없습니다: " + title);
            }
        }

        for(Movie s : movieList){
            System.out.println("영화명: " + s.getMvTitle() + " 별점: " + s.getMvStar());
        }

        //return값을 movieList로 바꾸면 됨  retrurn movieList
        //return recommendations;
        long movieFetchEnd = System.currentTimeMillis();
        System.out.println("DB에서 영화 정보 가져오기 시간: " + (movieFetchEnd - movieFetchStart) + " ms");

        long totalEnd = System.currentTimeMillis();
        System.out.println("총 recommendMovies 실행 시간: " + (totalEnd - totalStart) + " ms");

        return movieList;
    }

    @GetMapping("/main/recommend-cached")
    public List<Movie> recommendMoviesWithCache(@RequestParam int userId) {
        // 캐싱된 결과를 Redis 등에서 가져오는 방식
        // Redis에서 캐시 조회
        List<String> cachedRecommendations = movieCacheRecommenderService.getRecommendations(userId);

        if (cachedRecommendations == null) {
            // 캐시가 없으면 추천 알고리즘 직접 실행 후 캐시에 저장
            cachedRecommendations = MovieCacheRecommenderService.recommendMovies(userId);
            redisTemplate.opsForValue().set("recommend:" + userId, cachedRecommendations, 1, TimeUnit.DAYS);
        }   List<Movie> movieList = new ArrayList<>();

        for (String str : cachedRecommendations) {
            String[] parts = str.split(", ");
            String title = parts[0].split(": ")[1].replace("\"", "");
            Movie movie = movieService.getMovieTitle(title);
            if (movie != null) {
                movieList.add(movie);
            }
        }

        return movieList;
    }
}