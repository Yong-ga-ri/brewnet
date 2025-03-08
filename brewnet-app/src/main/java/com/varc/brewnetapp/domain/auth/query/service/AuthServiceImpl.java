package com.varc.brewnetapp.domain.auth.query.service;

import com.varc.brewnetapp.domain.auth.query.mapper.AuthenticationMapper;
import com.varc.brewnetapp.domain.auth.query.vo.MemberVO;
import com.varc.brewnetapp.domain.auth.query.vo.RoleVO;
import com.varc.brewnetapp.security.domain.CustomUser;
import com.varc.brewnetapp.domain.auth.query.dto.MemberInfoDTO;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service(value="queryAuthenticationService")
public class AuthServiceImpl implements AuthService {
    private final AuthenticationMapper authenticationMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AuthServiceImpl(AuthenticationMapper authenticationMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authenticationMapper = authenticationMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        if (Objects.equals(loginId, "NONE_PROVIDED")) {
            throw new UsernameNotFoundException(loginId);
        }

        MemberVO loginMember = authenticationMapper.selectMemberByIdWithAuthorities(loginId);

        if (loginMember == null) {
            throw new UsernameNotFoundException(loginId);
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (RoleVO roleVO : loginMember.getRoleVOSet()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(roleVO.getRole()));
        }

        return new CustomUser(
                loginMember.getMemberCode(),
                loginMember.getId(),
                loginMember.getPassword(),
                loginMember.getName(),
                grantedAuthorities
        );
    }

    @Override
    public boolean isMatchInputPasswordWithSavedPassword(String tryingPassword, UserDetails savedUser) {
        return bCryptPasswordEncoder.matches(tryingPassword, savedUser.getPassword());
    }

    @Override
    @Transactional
    public List<String> getAuths() {
        return authenticationMapper.selectAuths();
    }


}
