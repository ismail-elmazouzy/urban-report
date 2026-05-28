package com.urbanreport.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println(">>> PATH: " + request.getRequestURI());
        System.out.println(">>> AUTH HEADER: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            System.out.println(">>> TOKEN: " + token.substring(0, Math.min(20, token.length())));

            try {
                if (jwtUtil.validateToken(token)) {
                    String email = jwtUtil.extractEmail(token);
                    System.out.println(">>> EMAIL: " + email);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println(">>> AUTH OK ✅");
                } else {
                    System.out.println(">>> TOKEN INVALID ❌");
                }
            } catch (Exception e) {
                System.out.println(">>> ERROR: " + e.getMessage());
            }
        } else {
            System.out.println(">>> NO TOKEN ❌");
        }

        filterChain.doFilter(request, response);
    }
}