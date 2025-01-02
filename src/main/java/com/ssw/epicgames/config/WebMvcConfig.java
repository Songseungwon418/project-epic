package com.ssw.epicgames.config;

import com.ssw.epicgames.interceptors.SessionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /** interceptors 폴더의 SessionInterceptor 클래스를 사용하기 위해 설정 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor()) // 사용할 Interceptor 생성
                .addPathPatterns("/purchase/**", "/page/**", "/article/write", "/game/addGame"); // 로그인해야 접속할 경로
    }
}
