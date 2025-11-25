package com.studyplanner.repositories;

import com.studyplanner.models.ERole;
import com.studyplanner.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
	Optional<Role> findByName(ERole name);
}

