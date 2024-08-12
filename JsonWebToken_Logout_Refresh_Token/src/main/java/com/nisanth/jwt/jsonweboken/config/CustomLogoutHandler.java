package com.nisanth.jwt.jsonweboken.config;

import com.nisanth.jwt.jsonweboken.model.Token;
import com.nisanth.jwt.jsonweboken.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenRepository tokenRepository;

    public CustomLogoutHandler(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7);
        // get storedToken from database
        Token storedToken = tokenRepository.findByAccessToken(token).orElse(null);

        // invalidate the token i.e make logout true
        if(storedToken != null) {
            storedToken.setLoggedOut(true);
            // save the token
            tokenRepository.save(storedToken);
        }
    }
}
