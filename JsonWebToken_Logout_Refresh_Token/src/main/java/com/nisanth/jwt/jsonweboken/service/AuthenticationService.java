package com.nisanth.jwt.jsonweboken.service;

import com.nisanth.jwt.jsonweboken.model.AuthenticationResponse;
import com.nisanth.jwt.jsonweboken.model.Token;
import com.nisanth.jwt.jsonweboken.model.User;
import com.nisanth.jwt.jsonweboken.repository.TokenRepository;
import com.nisanth.jwt.jsonweboken.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        String accessToken=jwtService.generateAccessToken(user);
        String refreshToken= jwtService.generateRefreshToken(user);



        // save the generated token
        saveUserToken(accessToken,refreshToken,user);

        return new AuthenticationResponse(accessToken,refreshToken);

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
        String accessToken= jwtService.generateAccessToken(user);
        String refreshToken= jwtService.generateRefreshToken(user);

        // revoke all token by user
        revokeAllTokenByUser(user);
        // save the user token
        saveUserToken(accessToken,refreshToken,user);
        // pas through the constructor
        return new AuthenticationResponse(accessToken,refreshToken);
    }


    private void saveUserToken(String accessToken, String refreshToken, User user) {
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }



    // find the token for user
    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllAccessTokensByUser(user.getId());
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t-> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    public ResponseEntity refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        // extract the token from authorization header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        // extract username from token
        String username = jwtService.extractUsername(token);

        // check if the user exist in database
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No user found"));

        //now  check if the token is valid
        if (jwtService.isValidRefreshToken(token, user)) {
            // generate access token
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

              revokeAllTokenByUser(user);
              saveUserToken(accessToken, refreshToken,user);

            return new ResponseEntity(new AuthenticationResponse(accessToken, refreshToken), HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    }
