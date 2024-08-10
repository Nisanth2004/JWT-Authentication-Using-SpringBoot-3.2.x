package com.nisanth.jwt.jsonweboken.repository;

import com.nisanth.jwt.jsonweboken.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByUsername(String username);

}
