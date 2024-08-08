package com.nisanth.jwt.jsonweboken.service;

import com.nisanth.jwt.jsonweboken.model.AuthenticationResponse;
import com.nisanth.jwt.jsonweboken.model.User;
import com.nisanth.jwt.jsonweboken.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
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

        String token=jwtService.generateToken(user);
        return new AuthenticationResponse(token);

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

        return new AuthenticationResponse(token);
    }
}
