package com.example.taskmanager.task_manager;

// Required to start the Spring Boot application.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Needed to configure beans and scan packages.
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

// Necessary to configure CORS (Cross-Origin Resource Sharing), 
// useful for frontend-backend communications on different domains.
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @SpringBootApplication indicates that this class is the entry point for the Spring Boot application.
/*
 * Includes three other entries:
 * @Configuration: Allows to define Spring configurations through annotated methods with @Bean.
 * @EnableAutoConfiguration: Enable automatic configuration of Spring Boot.
 * @ComponentScan: Search for components (controllers, services, etc.) within the main package and sub-packages.
*/
@SpringBootApplication
/*
 * @ComponentScan:
 * Explicitly specifies the packages to be scanned for Spring components (such as @RestController, @Service, @Component).
 * Includes the packages security, controller, and service.
*/
@ComponentScan(basePackages = {"com.example.taskmanager.security", "com.example.taskmanager.task_manager.controller", "com.example.taskmanager.task_manager.service"})
public class TaskManagerApplication {
	/*
	 * Start the application by calling the SpringApplication run method. 
	 * Load all the Spring configurations defined in context.
	 * */
    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
    
    // The corsConfigurer method defines a custom configuration to manage the CORS.
    // Allows the frontend (running on http://127.0.0.1:5500) to communicate with the backend.
    // @Beans record this method as a bean in the Spring context.
    @Bean
    public WebMvcConfigurer corsConfigurer() {
    	/*
    	 * addCorsMappings: Configure CORS rules: 
    	 *   "/**": Allows all endpoints to accept requests.
    	 *   allowedOrigins: Allows requests from a specific source (your frontend in this case).
    	 *   allowedMethods: Specifies the allowed HTTP methods.*/
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:5500")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .maxAge(3600);
            }
        };
    }
}
