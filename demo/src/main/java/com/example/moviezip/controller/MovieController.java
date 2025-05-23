package com.example.moviezip.controller;

import com.example.moviezip.domain.Movie;
import com.example.moviezip.service.CustomUserDetailsService;
import com.example.moviezip.service.MovieImpl;
import com.example.moviezip.service.WishImpl;
import com.example.moviezip.service.recommend.MovieRecommenderService;
import com.example.moviezip.util.jwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/")
public class MovieController {
    private MovieImpl movieService;
    private WishImpl wishService;

    @Autowired
    public void setWishService(WishImpl wishService) {
        this.wishService = wishService;
    }
    @Autowired
    public void setMovieService(MovieImpl movieService) {
        this.movieService = movieService;
    }
    @Autowired
    private jwtUtil jwtUtil;
    @Autowired
    private  CustomUserDetailsService customUserDetailsService;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("movie/{mvId}")
    public Movie getMovie(@PathVariable int mvId,@RequestHeader("Authorization") String token) throws Exception {
        System.out.println("Entering getMovie method with mvId: " + mvId);

        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        Long userIdFromToken = jwtUtil.extractUserId(jwt); // jwtUtil은 JWT 유틸리티 클래스


        Movie mv = movieService.getAllMoviedetail(mvId);
        if (mv != null) {
            System.out.println("영화제목222: " + mv.getMvTitle());
            System.out.println("영화제목: " + mv.getMvStar());
            System.out.println(mv.getMvDetail());
            System.out.println(mv.getMvId());
            System.out.println(mv.getOpenDate());
            System.out.println(mv.getMvDirector());
            System.out.println(mv.getGenre2());
        } else {
            System.out.println("영화 정보를 찾을 수 없습니다.");
        }
        return mv;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/main")
    public List<Movie> getRecentMovie(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        Long userIdFromToken = jwtUtil.extractUserId(jwt); // jwtUtil은 JWT 유틸리티 클래스

        List<Movie> movieList = movieService.getRecentMovie();
        for(Movie movie : movieList) {
            System.out.println("최신영화"+movie.getMvTitle());
        }
        return movieList;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("search/{mvTitle}")
    public List<Movie> getSearchMovie(@PathVariable String mvTitle,@RequestHeader("Authorization") String token) throws Exception {
        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        Long userIdFromToken = jwtUtil.extractUserId(jwt); // jwtUtil은 JWT 유틸리티 클래스


        System.out.println("Entering getMovie method with mvTitle: " + mvTitle);
        List<Movie> movieList = movieService.searchMoviesByTitle(mvTitle);
        for(Movie mv : movieList) {
            System.out.println("검색제목검색: " + mv.getMvTitle());
            System.out.println("검색제목별점: " + mv.getMvStar());
            System.out.println(mv.getMvId());
            System.out.println(mv.getMvDirector());
        }
        return movieList;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("movie/{mvId}/wish")
    public int checkMyMovieWish(@PathVariable int mvId, @RequestParam int userId, @RequestHeader("Authorization") String token) throws Exception {

        System.out.println("Entering getMovie method with mvId: " + mvId);

        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        Long userIdFromToken = jwtUtil.extractUserId(jwt); // jwtUtil은 JWT 유틸리티 클래스


        int ch = wishService.checkMyWish(userId, mvId);
        System.out.println("찜" + ch);
        return ch;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("movie/{mvId}/wish/add")
    public int insertMyMovieWish(@PathVariable int mvId, @RequestParam int userId) throws Exception {
        System.out.println("Entering getMovie method with mvId: " + mvId);
        int ch = wishService.saveWishMovie(userId, mvId);
        if(ch == 1){
            System.out.println("찜 성공" + ch);
        }else {
            System.out.println("찜 실패");
        }
        return ch;
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("movie/{mvId}/wish/delete")
    public int deleteMyMovieWish(@PathVariable int mvId, @RequestParam int userId) throws Exception {
        System.out.println("Entering getMovie method with mvId: " + mvId);
        int ch = wishService.deleteWishMovie(userId, mvId);
        if(ch == 1){
            System.out.println("찜 삭제 성공" +ch);
        }else {
            System.out.println("찜 삭제 실패");
        }
        return ch;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/movie/wish")
    public List<Movie> getWishMovie(@RequestParam int userId, @RequestHeader("Authorization") String token) {

        String jwt = token.substring(7); // "Bearer " 제거
        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        String userNameFromToken = jwtUtil.extractUsername(jwt); // jwtUtil은 JWT 유틸리티 클래스

        // 토큰 유효성 검사
        if (!jwtUtil.validateToken(jwt)) {
            throw new RuntimeException("Invalid or expired token");  // 적절한 예외 처리
        }
        List<Movie> movieList = wishService.getWishMovie(userId);
        for (Movie movie : movieList) {
            System.out.println("보관함 영화: " + movie.getMvTitle());
        }
        // movieList 중 5개만 movieList2에 넣어서 return
        List<Movie> movieList2 = movieList.stream().limit(5).toList();
        return movieList2;
    }
}