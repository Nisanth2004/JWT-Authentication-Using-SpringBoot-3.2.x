package com.nisanth.jwt.jsonweboken.service;

import com.nisanth.jwt.jsonweboken.model.AuthenticationResponse;
import com.nisanth.jwt.jsonweboken.model.Token;
import com.nisanth.jwt.jsonweboken.model.User;
import com.nisanth.jwt.jsonweboken.repository.TokenRepository;
import com.nisanth.jwt.jsonweboken.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    public AuthenticationService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, TokenRepository tokenRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
    }
// User for Register

    public AuthenticationResponse register(User request)
    {

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));


        user.setRole(request.getRole());

        user = repository.save(user);

        String jwt=jwtService.generateToken(user);


        // save the generated token
        Token token = saveUserToken(jwt,user);

        return new AuthenticationResponse(jwt);

    }



    // User for login
    public AuthenticationResponse authenticate(User request)
    {
        // get the request from user
       authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                       request.getUsername(),
                       request.getPassword()
               )
       );

       // get the user from database to valid
        User user=repository.findByUsername(request.getUsername()).orElseThrow();
        String token= jwtService.generateToken(user);

        // revoke all token by user
        revokeAllTokenByUser(user);
        // save the user token
        saveUserToken(token, user);
        return new AuthenticationResponse(token);
    }


    private Token saveUserToken(String jwt, User user) {
        Token token=new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
        return token;
    }


    // find the token for user
    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllTokensByUser(user.getId());
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t-> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }
}
