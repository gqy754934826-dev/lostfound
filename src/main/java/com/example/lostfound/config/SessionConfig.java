package com.example.lostfound.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Session配置类
 */
@Configuration
public class SessionConfig {
    
    /**
     * 配置Session Cookie序列化器
     *
     * @return Cookie序列化器
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("JSESSIONID");
        serializer.setCookiePath("/");
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        // 添加SameSite=None和secure设置以支持跨域请求
        serializer.setSameSite("None");
        serializer.setUseSecureCookie(true);
        return serializer;
    }
}