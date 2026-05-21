package com.carrentalsystem.app.repository;

import com.carrentalsystem.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Additional query methods can be defined here if needed
    Optional<User> findByEmail(String email);

}
