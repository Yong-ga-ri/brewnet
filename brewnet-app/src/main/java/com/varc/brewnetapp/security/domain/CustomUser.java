package com.varc.brewnetapp.security.domain;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@ToString
public class CustomUser implements UserDetails {
    private final int memberCode;
    private final String username;  // id
    private final String password;
    private final String name;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUser(
            int memberCode,
            String username,
            String password,
            String name,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super();
        this.memberCode = memberCode;
        this.username = username;
        this.password = password;
        this.name = name;
        this.authorities = authorities;
    }
}
