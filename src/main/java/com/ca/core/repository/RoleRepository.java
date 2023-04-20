package com.ca.core.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.ca.core.models.ERole;
import com.ca.core.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
	Optional<Role> findByName(ERole name);
}
