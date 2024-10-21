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

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Estrarre l'header Authorization
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Controlla se l'header inizia con "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);  // Estrai il token senza "Bearer "
            username = jwtUtil.extractUsername(token);  // Estrai l'username dal token JWT
        }

        // Se il contesto di sicurezza non ha già un utente autenticato
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            // Verifica se il token è valido
            if (jwtUtil.validateToken(token, userDetails)) {
                // Crea il token di autenticazione con i ruoli (authorities)
                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Imposta l'utente autenticato nel contesto di sicurezza
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Passa la richiesta alla catena di filtri successivi
        chain.doFilter(request, response);
    }
}
