package net.foodeals.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://147.93.55.75:8080",
                        "http://147.93.55.75:3000",
                        "http://localhost:8000",
                        "https://www.elitecodersacademy.com",
                        "http://www.elitecodersacademy.com",
                        "http://elitecodersacademy.com",
                        "https://elitecodersacademy.com",
                        "http://localhost:4200",
                        "147.93.55.75"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .exposedHeaders("Access-Control-Allow-Origin")
                .allowCredentials(true);
    }
}