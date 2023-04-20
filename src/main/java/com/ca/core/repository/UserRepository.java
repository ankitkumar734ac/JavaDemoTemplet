package com.ca.core.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ca.core.models.User;

public interface UserRepository extends MongoRepository<User, String> {
	Optional<User> findByEmail(String email);

	Boolean existsByEmail(String email);

	Optional<User> findByEmailAndPassword(String email, String password);

}
