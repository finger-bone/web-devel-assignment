package com.webdev.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webdev.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);

	boolean existsByUsername(String username);

}
