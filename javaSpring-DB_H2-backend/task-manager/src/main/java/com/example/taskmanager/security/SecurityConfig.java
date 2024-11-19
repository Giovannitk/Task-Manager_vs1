package com.example.taskmanager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

// Configuration of HTTP level security (to protect your APIs).
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

// To encrypt passwords.
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Management of CORS.
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity // Enable Spring Security

// Protect methods with annotations like @PreAuthorize..
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	// Dependency injection: Inject the custom JwtRequestFilter filter 
	// to manage JWT authentication.
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	// Disable CSRF protection, as REST APIs generally do not use cookies for authentication.
        http.csrf().disable() // Disable CSRF: Not required for applications without sessions.
            .cors() // Enable CORS:Allows cross-origin requests.
            .and()
            .authorizeRequests()
            // permitAll: Allows authentication-less access to specific endpoints.
            .antMatchers("/userAdmin", "/login", "/register", "/h2-console/**","/confirm").permitAll()  
            .anyRequest().authenticated()  // Require authentication for all other requests.
            .and()
         // Allow frames for /h2-console ( Access to the H2 database via a web interface is allowed.).
            .headers().frameOptions().sameOrigin()  
            .and()
            // STATELESS: The app uses JWT instead of sessions.
            // Disables session usage, ideal for token-based REST APIs.
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);  // Disabilita le sessioni

        // Added custom JWT filter before standard authentication filter of Spring Security. 
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt to encrypt and verify passwords.
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // Get Spring authentication manager.
    	return authenticationConfiguration.getAuthenticationManager();
    }

    // CORS configuration to allows requests from the frontend.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500"));  // Aggiungi il frontend
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        // Enable credentials to send cookies or tokens.
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

/*
 * Problem1:
The endpoints configured with permitAll in SecurityConfig are correct for the current context. 
However, it is important to note that:
If you add new endpoints, you’ll need to remember to update them here.

Solution:
Consider using constants or enum to list public endpoints, 
so it will be easier to update them.*/

/*
 * Problem2:
The CORS configuration is present in:
TaskManagerApplication (corsConfigurer method).
WebConfig (corsConfigurer method).
SecurityConfig (corsConfigurationSource method).

Solution:
You can centralize the CORS configuration in one place, preferably in SecurityConfig, since Spring Security also manages filters for CORS. This avoids conflicts and makes the code cleaner.

To do:
Remove corsConfigurer methods from TaskManagerApplication and WebConfig.
Use corsConfigurationSource only in SecurityConfig.

Step1: Remove addCorsMapping method from WebConfig and then the entire class.
Also remove the method in the TaskManagerApplication class.

Step2: Add or modify the corsConfigurationSource 
method in the SecurityConfig class to include all details.

Step3: Make sure CORS is enabled in SecurityConfig when configuring HttpSecurity.

SecurityConfig updated:
package com.example.taskmanager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    // Configura la catena di filtri di sicurezza
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // Disabilita la protezione CSRF
            .cors() // Abilita il supporto CORS
            .and()
            .authorizeRequests()
            .antMatchers("/userAdmin", "/login", "/register", "/h2-console/**", "/confirm").permitAll() // Rotte pubbliche
            .anyRequest().authenticated() // Tutte le altre rotte richiedono autenticazione
            .and()
            .headers().frameOptions().sameOrigin() // Permetti frame per /h2-console
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Disabilita la gestione delle sessioni

        // Aggiungi il filtro JWT prima del filtro standard di autenticazione di Spring Security
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configurazione del password encoder (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configurazione dell'AuthenticationManager per la gestione dell'autenticazione
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Configurazione CORS centralizzata
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500")); // Origine consentita (frontend)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Metodi consentiti
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // Header consentiti
        configuration.setAllowCredentials(true); // Permetti cookie/token

        // Associa la configurazione alle rotte
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

 */
