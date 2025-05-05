package com.group02.mindmingle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    /**
     * 配置CORS
     */
    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    // registry.addMapping("/**")
    // .allowedOrigins("http://localhost:3000")
    // .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    // .allowedHeaders("*")
    // .allowCredentials(true);
    // }

    /**
     * 添加自定义过滤器，处理iframe嵌入权限
     */
    @Bean
    public FilterRegistrationBean<XFrameOptionsFilter> xFrameOptionsFilter() {
        FilterRegistrationBean<XFrameOptionsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XFrameOptionsFilter(frontendUrl));
        registrationBean.addUrlPatterns("/*"); // 应用于所有URL
        return registrationBean;
    }

    /**
     * 自定义X-Frame-Options过滤器，允许特定URL嵌入iframe
     */
    public static class XFrameOptionsFilter extends OncePerRequestFilter {

        private final String frontendUrl;

        public XFrameOptionsFilter(String frontendUrl) {
            this.frontendUrl = frontendUrl;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                FilterChain filterChain)
                throws ServletException, IOException {

            // 设置允许iframe嵌入的响应头
            response.setHeader("X-Frame-Options", null); // 清除可能存在的限制性头

            // 使用更现代的Content-Security-Policy指令
            response.setHeader("Content-Security-Policy",
                    "frame-ancestors 'self' " + frontendUrl);

            filterChain.doFilter(request, response);
        }
    }
}
