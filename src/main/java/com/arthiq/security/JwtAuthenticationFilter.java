package com.arthiq.security;

import com.arthiq.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private JwtUtil jwtUtil;
    private UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setAuthenticationManager(authenticationManager);
    }

    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public void setUserDetailsService(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            String jwt = parseJwt(httpRequest);
            System.out.println("JWT Token received: " + jwt); // DEBUG

            if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
                System.out.println("Token is valid"); // DEBUG
                String username = jwtUtil.getUsernameFromJwtToken(jwt);
                System.out.println("Username from token: " + username); // DEBUG

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                var authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Authentication set successfully"); // DEBUG
            } else {
                System.out.println("Token validation failed or token is null"); // DEBUG
            }
        } catch (Exception e) {
            System.out.println("Error in JWT filter: " + e.getMessage()); // DEBUG
            e.printStackTrace();
        }

        chain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        System.out.println("Authorization header: " + headerAuth); // DEBUG

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
