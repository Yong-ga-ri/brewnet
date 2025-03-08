package com.varc.brewnetapp.domain.auth.query.service;

import java.util.List;

import com.varc.brewnetapp.domain.auth.query.dto.MemberInfoDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException;

    boolean isMatchInputPasswordWithSavedPassword(String tryingPassword, UserDetails savedUser);
    List<String> getAuths();

    MemberInfoDTO getMemberInfoDTO(UserDetails userDetails, String refreshToken);
    MemberInfoDTO getMemberInfoDTOBy(String loginId);

}
