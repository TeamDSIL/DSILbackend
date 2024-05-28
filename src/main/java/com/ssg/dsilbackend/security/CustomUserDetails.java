package com.ssg.dsilbackend.security;

import com.ssg.dsilbackend.domain.Members;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Members members;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return members.getPermission().getPermission().name();
            }
        });
        return collection;
    }

    public String getEmail() {
        return members.getEmail();
    }

    @Override
    public String getPassword() {
        return members.getPassword();
    }

    @Override
    public String getUsername() {
        return members.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return members.isStatus(); // status 값이 false인 경우 로그인을 막음
    }
}
