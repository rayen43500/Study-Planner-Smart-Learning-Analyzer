package com.studyplanner.repositories;

import com.studyplanner.models.Subject;
import com.studyplanner.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubjectRepository extends MongoRepository<Subject, String> {
	List<Subject> findByUser(User user);
}

