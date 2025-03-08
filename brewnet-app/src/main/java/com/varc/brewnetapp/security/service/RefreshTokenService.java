package com.varc.brewnetapp.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.varc.brewnetapp.domain.auth.query.dto.MemberInfoDTO;
import com.varc.brewnetapp.domain.auth.query.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;
    private final Environment environment;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Autowired
    public RefreshTokenService(StringRedisTemplate redisTemplate, Environment environment, AuthService authService, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.environment = environment;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    public void saveRefreshToken(UserDetails user, String refreshToken) {
        String loginId = user.getUsername();
        MemberInfoDTO memberInfoDTO = authService.getMemberInfoDTO(user, refreshToken);
        long expirationTime = Long.parseLong(Objects.requireNonNull(environment.getProperty("token.refresh.expiration_time")));
        try {
            String jsonValue = objectMapper.writeValueAsString(memberInfoDTO);
            redisTemplate.opsForValue().set(loginId, jsonValue, expirationTime, TimeUnit.MILLISECONDS);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void deleteRefreshToken(String loginId) {
        redisTemplate.delete(loginId);
    }

    public boolean checkRefreshTokenInRedis(String loginId, String refreshToken) {
        return refreshToken.equals(getRefreshToken(loginId));
    }

    private String getRefreshToken(String loginId) {
        MemberInfoDTO memberInfoDTO = getMemberInfoFromRedis(loginId);

        if (memberInfoDTO == null) return null;
        else return memberInfoDTO.getRefreshToken();
    }

    private MemberInfoDTO getMemberInfoFromRedis(String loginId) {
        String json = redisTemplate.opsForValue().get(loginId);
        if (json == null) {
            return null;
        } else {
            try {
                return objectMapper.readValue(json, MemberInfoDTO.class);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("RefreshTokenInfo 파싱 에러", e);
            }
        }
    }
}
