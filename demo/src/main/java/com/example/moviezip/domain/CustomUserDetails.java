package com.example.moviezip.domain;

import org.apache.hadoop.shaded.org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

//시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
//로그인 진행이 완료가 되면 시큐리티 session을 만들어준다.(Security ContextHolder)
//오브젝트가 정해져 있음 -> Authentication 타입의 객체
//Authenticaion 안에 User 정보가 있어야함
//User 오브젝트 타입 -> UserDetails 타입 객체

public class CustomUserDetails implements UserDetails {
    private final User user;
    // 기본 생성자 추가
    public CustomUserDetails() {
        this.user = new User(); // 기본 User 객체 생성
    }

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> user.getRole().name()); // 역할을 GrantedAuthority로 변환
    }

    public Long getUserId(){
        return user.getId();
    }
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }



    @Override
    public boolean isAccountNonExpired() {
        return true; //계정 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; //계정 잠김 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; //계정 활성화 여부
    }

//    public Long getUser() {
//        return user.getId();
//    }
    public String getUser() {
        return user.getUserId();
    }

    public User getUser2() {
        return user;
    }
}
