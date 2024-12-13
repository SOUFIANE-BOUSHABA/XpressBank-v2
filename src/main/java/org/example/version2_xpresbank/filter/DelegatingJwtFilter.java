package org.example.version2_xpresbank.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.version2_xpresbank.Service.JwtService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class DelegatingJwtFilter extends OncePerRequestFilter {

    private final JwtRequestFilter jwtRequestFilter;
    private final JwtOAuth2Filter jwtOAuth2Filter;
    private final JwtService jwtService;

    public DelegatingJwtFilter(JwtRequestFilter jwtRequestFilter, @Lazy JwtOAuth2Filter jwtOAuth2Filter, JwtService jwtService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.jwtOAuth2Filter = jwtOAuth2Filter;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            if (jwtService.isValidToken(token)) {
                jwtRequestFilter.doFilter(request, response, filterChain);
            } else {
                jwtOAuth2Filter.doFilter(request, response, filterChain);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
