package com.stream.four.repository;

import com.stream.four.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    boolean existsUserById(String id);

    boolean existsByEmail(String email);
}