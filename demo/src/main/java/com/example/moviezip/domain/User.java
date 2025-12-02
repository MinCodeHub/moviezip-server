package com.example.moviezip.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@NoArgsConstructor
@Alias("User")
public class User {
    private Long id;
    private String userId;
    private String password;
    private String nickname;
    private String hint;
    private Role role; // 역할을 추가

    public User(Long id) {
        this.id = id;
    }

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
