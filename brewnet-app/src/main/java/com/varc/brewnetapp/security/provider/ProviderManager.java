package com.varc.brewnetapp.security.provider;

import com.varc.brewnetapp.security.exception.NotAuthenticatedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ProviderManager는 {@link AuthenticationManager}를 구현하여,
 * 등록된 여러 {@link AuthenticationProvider} 중에서 요청된 인증 정보를 처리할 수 있는 적절한 프로바이더를 선택하여 인증을 수행합니다.
 * <p>
 * 요청된 인증 토큰을 처리할 수 있는 프로바이더를 순차적으로 조회하며, 인증에 성공하면 해당 인증 객체를 반환합니다.
 * 만약 적절한 프로바이더가 없거나 인증에 실패하면 {@link NotAuthenticatedException}을 발생시킵니다.
 * </p>
 */
@Slf4j
@Component
public class ProviderManager implements AuthenticationManager {
    private final List<AuthenticationProvider> providerList;

    /**
     * ProviderManager 생성자.
     *
     * @param providerList 애플리케이션 컨텍스트에 등록된 모든 AuthenticationProvider 목록
     */
    @Autowired
    public ProviderManager(List<AuthenticationProvider> providerList) {
        this.providerList = providerList;
    }

    /**
     * 인증 요청을 처리할 수 있는 적절한 AuthenticationProvider를 찾아 인증을 수행합니다.
     *
     * @param authentication 인증 요청 객체
     * @return 인증에 성공한 {@link Authentication} 객체
     * @throws AuthenticationException 적절한 프로바이더가 없거나 인증에 실패한 경우 발생
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        for (AuthenticationProvider provider : providerList) {
            if (provider.supports(authentication.getClass())) {
                Authentication result = provider.authenticate(authentication);
                if (result != null && result.isAuthenticated()) {
                    return result;
                }
            }
        }

        throw new NotAuthenticatedException("Adequate provider not found");
    }
}
