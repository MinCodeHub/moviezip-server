package com.example.moviezip.service;
import com.example.moviezip.domain.Interest;
import algebra.lattice.Bool;
import com.example.moviezip.domain.Movie;
import com.example.moviezip.domain.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    public void updateUserPassword(Long id, String newPassword);
    public void updateUserNickname(Long id, String newNickname);

    public void updateInterest (Long id, String genre);

    public void deleteUser(Long id);

    public List<Movie> searchMoviesByTitle(String keyword);

    public void signup(User user);

    User getUserById(String id);

    User findByUserId(String userId);

    //회원가입
    ResponseEntity<String> joinProcess(User user);

    Long getIdByUsername(String username);

    void addInterest(Long id, String genre);
    Boolean findInterest(Long id);
    String findUserIdByInfo(String nickname, String hint);

    Boolean checkUserExistsById(String userId);

    User getUserById2(Long id);

    List<String> findInterest2(Long id);

    Long findAdminId();

    //모든 사용자들의 Long타입 id가져오기
    List<Long> getAllUserIds();
}
