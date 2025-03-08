package com.varc.brewnetapp.config;

import com.varc.brewnetapp.domain.auth.query.service.AuthService;
import com.varc.brewnetapp.security.service.RefreshTokenService;
import com.varc.brewnetapp.shared.interceptor.CachingMemberInfoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    public WebConfiguration(RefreshTokenService refreshTokenService, AuthService authService) {
        this.refreshTokenService = refreshTokenService;
        this.authService = authService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CachingMemberInfoInterceptor(refreshTokenService, authService))
                .addPathPatterns("/api/v1/franchise/**");
    }
}
