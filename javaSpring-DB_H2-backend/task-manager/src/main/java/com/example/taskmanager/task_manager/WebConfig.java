package com.example.taskmanager.task_manager;

// Similar considerations made for TaskManagerApplication dependencies, 
// used to define configurations and manage the CORS.
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration: indicates that this class contains Spring configurations.
// implements WebMvcConfigurer: Implements an interface to customize the 
// MVC configuration of Spring (for example, by adding CORS rules).
@Configuration
public class WebConfig implements WebMvcConfigurer {

	/*
	 * Similar to TaskManagerApplication:
	 * Configure the CORS rules, with some more details:
	 * allowedHeaders: Specifies the headers allowed in requests (e.g. Authorization for JWT tokens).
	 * allowCredentials: Allows the use of cookies or tokens in CORS context.*/
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://127.0.0.1:5500")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Authorization", "Content-Type")
                        .allowCredentials(true);
            }
        };
    }
}

/*
 * Both classes define a CORS configuration. This could lead to conflicts or unexpected behavior.
 * Solution: Keep the corsConfigurer method only in one of two classes (preferably in WebConfig to separate configuration from application code).
 * Use of WebConfig:
 * 
 * It is a class dedicated for MVC and CORS configuration, so it is the best choice for these configurations.
 * The TaskManagerApplication class should focus only on starting the application.
 * 
 * Update: View Class SecurityConfig.
*/


