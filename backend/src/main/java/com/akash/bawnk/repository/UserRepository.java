package com.akash.bawnk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akash.bawnk.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA automatically creates the query for us
    Optional<User> findByUsername(String username);
}