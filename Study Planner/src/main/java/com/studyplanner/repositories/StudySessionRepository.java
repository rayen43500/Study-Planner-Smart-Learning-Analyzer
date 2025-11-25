package com.studyplanner.repositories;

import com.studyplanner.models.StudySession;
import com.studyplanner.models.Subject;
import com.studyplanner.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface StudySessionRepository extends MongoRepository<StudySession, String> {
	List<StudySession> findByUser(User user);
	List<StudySession> findByUserAndDate(User user, LocalDate date);
	List<StudySession> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
	List<StudySession> findBySubject(Subject subject);
}

