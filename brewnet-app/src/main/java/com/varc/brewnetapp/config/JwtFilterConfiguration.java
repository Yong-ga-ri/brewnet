package com.varc.brewnetapp.config;

import com.varc.brewnetapp.shared.servlet.CustomJwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtFilterConfiguration {
    private final CustomJwtFilter customJwtFilter;

    @Autowired
    public JwtFilterConfiguration(CustomJwtFilter customJwtFilter) {
        this.customJwtFilter = customJwtFilter;
    }

    @Bean
    public FilterRegistrationBean<CustomJwtFilter> setFilter() {
        FilterRegistrationBean<CustomJwtFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(customJwtFilter);
        filterRegistrationBean.setOrder(0);
        filterRegistrationBean.addUrlPatterns("/api/*");
        return filterRegistrationBean;
    }

}
