package com.nisanth.jwt.jsonweboken.filter;

import com.nisanth.jwt.jsonweboken.service.JwtService;
import com.nisanth.jwt.jsonweboken.service.UserDetailsServiceImp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { // execute for every incoming request

    private final JwtService jwtService;
    private final UserDetailsServiceImp userDetailsService;


    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImp userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
                                    @NonNull HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                   @NonNull FilterChain filterChain)
            throws ServletException, IOException {


        // get the header in postman
        String authHeader = request.getHeader("Authorization");

        // check if its starts with Bearer after the token will present if not this method will be used
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request,response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        // check the user is authenticated,here it is not authenticated means
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {


            UserDetails userDetails = userDetailsService.loadUserByUsername(username);


            // means we had to authenticate the user

            if(jwtService.isValid(token, userDetails)) {
                // credentials is null because it is token based authentication
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);


    }
}