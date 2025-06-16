package com.example.appReceitas.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.appReceitas.security.FirebaseTokenFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<FirebaseTokenFilter> firebaseFilterRegistration(FirebaseTokenFilter filter) {
        FilterRegistrationBean<FirebaseTokenFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/receitas/*", "/comentarios/*"); // coloque aqui os endpoints que devem exigir login
        registration.setOrder(1);
        return registration;
    }
}
