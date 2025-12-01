package com.example.moviezip.controller;

import com.example.moviezip.domain.Review;
import com.example.moviezip.domain.User;
import com.example.moviezip.service.ReviewImpl;
import com.example.moviezip.service.UserService;
import com.example.moviezip.service.UserServiceImpl;
import com.example.moviezip.util.jwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class ReviewController {

    private final ReviewImpl reviewImpl;
    private final UserService userService;
    @Autowired
    private jwtUtil jwtUtil;
    @Autowired
    public ReviewController(ReviewImpl reviewImpl, UserService userService) {
        this.reviewImpl = reviewImpl;
        this.userService = userService;

    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("movie/{mvId}/regReview")
    public void insertMyReview(@RequestHeader("Authorization") String token,@RequestBody Review review, @PathVariable Long mvId) {
        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        jwtUtil.extractUserId(jwt); // jwtUtil은 JWT 유틸리티 클래스

        try {
            User user = userService.getUserById(review.getWriter());
            String wrtier = user.getUserId();
            review.setWriter(wrtier);
            review.setMvId(mvId);
            review.setIs_Critic("N");
            reviewImpl.insertMyReview(review);
            System.out.println("리뷰가 성공적으로 저장되었습니다: " + review.getContent());
        } catch (Exception e) {
            System.out.println("실패");
            e.printStackTrace();
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("wish/myReviewList")
    public List<Review> getMyReviewList(@RequestHeader("Authorization") String token,@RequestParam String userId) {
        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        jwtUtil.extractUserId(jwt); // jwtUtil은 JWT 유틸리티 클래스

        User user = userService.getUserById(userId);
        String wrtier = user.getUserId();

        List<Review> myReviewList = reviewImpl.getMyReviewList(wrtier);
        try {
            for (Review rv : myReviewList) {
                System.out.println("내가 쓴 리뷰 :" + rv.getMvTitle()+rv.getContent()+rv.getRvId());
            }
        } catch (Exception e) {
            System.out.println("리뷰 실패:");
            e.printStackTrace();
        }
        return myReviewList;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("movie/{mvId}/critics")
    public List<Review> getCriticsReview(@RequestHeader("Authorization") String token,@PathVariable int mvId) throws Exception {
        System.out.println("Entering getMovie method with mvId: " + mvId);
        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        jwtUtil.extractUserId(jwt); // jwtUtil은 JWT 유틸리티 클래스

        List<Review> rv = reviewImpl.getCriticReviews(mvId);

        for (Review r : rv) {
            System.out.println("내용" + r.getContent());
            System.out.println("이름" + r.getRvTitle());
            System.out.println("별점" + r.getRvStar());
        }
        return rv;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("wish/myReviewList/ReviewBoxDetail/{rvId}")
    public Review getReviewDetail(@RequestHeader("Authorization") String token,@PathVariable int rvId) throws Exception {
        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        jwtUtil.extractUserId(jwt); // jwtUtil은 JWT 유틸리티 클래스

        System.out.print(rvId);
        Review rv = reviewImpl.getReviewDetail(rvId);
        System.out.println("리뷰 상세 정보:" + rv.getContent());
        return rv;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/wish/MyReviewBoxUpdate/{rvId}")
    public int updateReview(@RequestHeader("Authorization") String token,@PathVariable int rvId, @RequestBody Review rv) throws Exception {
        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        jwtUtil.extractUserId(jwt); // jwtUtil은 JWT 유틸리티 클래스

        int cnt = reviewImpl.updateReview(rv);
        System.out.println("리뷰 수정 결과:" + rv.getContent());
        return cnt;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping("wish/myReviewList/delete")
    public void deleteReview(@RequestHeader("Authorization") String token,@RequestParam int rvId) throws Exception {
        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        jwtUtil.extractUserId(jwt); // jwtUtil은 JWT 유틸리티 클래스

        System.out.println("삭제");
        reviewImpl.deleteReview(rvId);
    }

}
