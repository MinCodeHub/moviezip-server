package com.example.moviezip.controller;

import com.example.moviezip.domain.Movie;
import com.example.moviezip.domain.Reservation;
import com.example.moviezip.domain.redis.SeatLockUpdate;
import com.example.moviezip.service.MovieImpl;
import com.example.moviezip.service.ReservationImpl;
import com.example.moviezip.service.TheaterImpl;
import com.example.moviezip.util.jwtUtil;
import org.apache.hadoop.yarn.api.records.ReservationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/")
public class ReservationController {
    private ReservationImpl reservationImpl;
    private TheaterImpl theaterImpl;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private jwtUtil jwtUtil;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    public void setMovieService(ReservationImpl reservationImpl, TheaterImpl theaterImpl) {
        this.reservationImpl = reservationImpl;
        this.theaterImpl = theaterImpl;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("movie/reserve")
    public int insertReserve(@RequestBody Reservation request,@RequestHeader("Authorization") String token) throws Exception {
        System.out.println("Entering getMovie method with mvId: " + request);
        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        jwtUtil.extractUserId(jwt); // jwtUtil은 JWT 유틸리티 클래스

        System.out.println("아이디사용자" + request.getId());
        Reservation reservation = new Reservation(
                request.getMvId(),
                request.getId(),
                request.getDateR(),
                request.getSeat(),
                request.getTime(),
                request.getRegion(),
                request.getPrice()
                );

        reservationImpl.insertReservation(reservation);

        System.out.println(request.getRegion() + "영화관!!!!!");

        Long theaterId= theaterImpl.getTheaterId(request.getRegion());
        System.out.println(theaterId + "영화관 아이디!!!!!!");

        System.out.println(request.getSeat() + "좌석!!!!!!!!!");
        System.out.println(request.getDateR());
        Date date = request.getDateR(); // Date 객체 가져오기
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd"); // 원하는 포맷 설정
        String formattedDate = formatter.format(date);
        System.out.println(formattedDate +"날짜"); // 포맷팅된 날짜 출력

        Long scrrenTimeId = theaterImpl.getScreenTimeId(request.getTime(), formattedDate,request.getMvId());
        System.out.println(scrrenTimeId + "스크린타임 아이디!!!!!!!!!!");

        String newTime = request.getTime().replace(":", "");
        String newFormattedDate = formattedDate.replace("/", "-");

        // Redis에 좌석 예약 상태 저장 (영화관 정보 포함)
        String redisKey = "seat:" + request.getMvId() + ":" + theaterId + ":" + scrrenTimeId + ":" +request.getSeat();
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(redisKey, "reserved");

        // 실시간으로 좌석 상태 업데이트를 전파 (영화관 정보 포함)
        messagingTemplate.convertAndSend(
                "/topic/reservation/" + request.getMvId() + "/" + request.getRegion()+ "/" + newFormattedDate + "/" + newTime,
                new SeatLockUpdate(request.getSeat(), theaterId, scrrenTimeId, "reserved")
        );
        System.out.println("Sending WebSocket update for seat: " + "/topic/reservation/" + request.getMvId() + "/" + request.getRegion()+ "/" + newFormattedDate + "/" + newTime);

        return 1;
    }

    @GetMapping("user/mypage")
    public List<Reservation> getReservationById(@RequestParam Long userId,@RequestHeader("Authorization") String token) throws Exception {
        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        jwtUtil.extractUserId(jwt); // jwtUtil은 JWT 유틸리티 클래스

        List<Reservation> reservations = reservationImpl.getReservationById(userId);
        System.out.println("아이디사용자" + userId);
        for (Reservation res : reservations) {
            System.out.println("Title: " + res.getMvTitle());
            System.out.println("Date: " + res.getDateR());
            System.out.println("Time: " + res.getTime());
            System.out.println("Seat: " + res.getSeat());
        }
        return reservations;
    }
    @GetMapping("/movie/reservedSeats/{mvId}")
    public List<String> getReservedSeats(
            @RequestHeader("Authorization") String token,
            @PathVariable String mvId,
            @RequestParam String region,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")Date date,
            @RequestParam String time) {
        String jwt = token.substring(7); // "Bearer " 제거

        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        jwtUtil.extractUserId(jwt); // jwtUtil은 JWT 유틸리티 클래스

        Long theaterId= theaterImpl.getTheaterId(region);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate = formatter.format(date);
        System.out.println(formattedDate + "영화관 조회 날짜");

        // 스크린 타임 ID를 가져옴
        Long scrrenTimeId = theaterImpl.getScreenTimeId(time, formattedDate, Long.valueOf(mvId));

        // Redis에서 예약된 좌석 정보를 가져옵니다.
        String redisKeyPattern = "seat:" + mvId + ":" + theaterId + ":" + scrrenTimeId + ":*";        Set<String> keys = redisTemplate.keys(redisKeyPattern);
        List<String> reservedSeats = new ArrayList<>();

        for (String key : keys) {
            // 키 형식에서 좌석 정보를 추출하여 리스트에 추가합니다.
            String[] parts = key.split(":");
            if (parts.length == 5) {
                String seat = parts[4]; // 좌석 ID 추출
                reservedSeats.add(seat);
                System.out.println(seat + "예약된 좌석");
            }
        }
        return reservedSeats;
    }
}
