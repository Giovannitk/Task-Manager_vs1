package com.example.taskmanager.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.taskmanager.task_manager.service.MyUserDetailsService;

// Filter that intercepts every HTTP request to extract and validate the JWT token.
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Extract Authorization header.
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Check the header: Search for a JWT token in the authorization 
        // header and whether the header starts with "Bearer".
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);  // Estrai il token senza "Bearer "
            username = jwtUtil.extractUsername(token);  // Estrai l'username dal token JWT
        }

        // Se il contesto di sicurezza non ha già un utente autenticato
        // If the security context doesn't already have an authenticated user.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            // Validates the token: If the token is valid, authenticates the user by setting the security context.
            if (jwtUtil.validateToken(token, userDetails)) {
                // Crea il token di autenticazione con i ruoli (authorities)
                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Imposta l'utente autenticato nel contesto di sicurezza
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Passes the request to the next filter chain.
        // Go to next filter: Processing of the request continues.
        chain.doFilter(request, response);
    }
}
