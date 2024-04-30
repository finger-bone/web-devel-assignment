package com.webdev.backend.service;

import com.webdev.backend.model.User;
import com.webdev.backend.repository.UserRepository;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public User registerUser(User user) {
		String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
		user.setPassword(hashedPassword);
		return userRepository.save(user);
	}

	public boolean verify(String username, String password) {
		User user = userRepository.findByUsername(username);
		return user != null && BCrypt.checkpw(password, user.getPassword());
	}

	public User getByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public boolean userExists(String username) {
		return userRepository.existsByUsername(username);
	}

    public void deleteUser(String originalUsername) {
		userRepository.delete(
			userRepository.findByUsername(originalUsername)
		);
    }

}