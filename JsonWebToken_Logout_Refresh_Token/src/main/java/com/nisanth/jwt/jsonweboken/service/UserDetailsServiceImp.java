package com.nisanth.jwt.jsonweboken.service;

import com.nisanth.jwt.jsonweboken.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class UserDetailsServiceImp implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImp.class);

    private final  UserRepository repository;


    public UserDetailsServiceImp(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);
        return repository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

    }
}
