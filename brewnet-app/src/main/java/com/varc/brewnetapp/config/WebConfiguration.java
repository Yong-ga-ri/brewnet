package com.varc.brewnetapp.config;

import com.varc.brewnetapp.security.service.RefreshTokenService;
import com.varc.brewnetapp.shared.interceptor.CachingMemberInfoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final RefreshTokenService refreshTokenService;

    public WebConfiguration(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CachingMemberInfoInterceptor(refreshTokenService))
                .addPathPatterns("/api/v1/franchise/**");
    }
}
