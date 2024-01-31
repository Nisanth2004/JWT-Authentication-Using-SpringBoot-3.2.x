package com.nisanth.SpringJwt.repository;

import com.nisanth.SpringJwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer>
{
    // search the user by username
    Optional<User> findByUsername(String username);

}
