package com.prototype.splitwise.config;

import com.mongodb.annotations.Immutable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class BaseMVCConfigurer implements WebMvcConfigurer {

    private static final String SECURED_PATH_REGEX = "/**";

    private static final List<String> EXCLUDE_PATTERNS =
            List.of("/ping",
                    "/users/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**");

    @Autowired List<HandlerInterceptor> interceptors;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        for (HandlerInterceptor interceptor : interceptors) {
            registry
                    .addInterceptor(interceptor)
                    .addPathPatterns(SECURED_PATH_REGEX)
                    .excludePathPatterns(EXCLUDE_PATTERNS)
                    .order(1);
        }
    }
}
