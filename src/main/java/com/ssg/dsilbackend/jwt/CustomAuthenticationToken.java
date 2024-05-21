package com.ssg.dsilbackend.jwt;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private String email;

    public CustomAuthenticationToken(Object principal, Object credentials, String email) {
        super(principal, credentials);
        this.email = email;
    }

}
