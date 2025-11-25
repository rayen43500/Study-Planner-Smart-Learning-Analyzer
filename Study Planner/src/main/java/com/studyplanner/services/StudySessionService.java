package com.studyplanner.services;

import com.studyplanner.dto.StudySessionDTO;
import com.studyplanner.models.StudySession;
import com.studyplanner.models.Subject;
import com.studyplanner.models.User;
import com.studyplanner.repositories.StudySessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudySessionService {

	private final StudySessionRepository studySessionRepository;

	public List<StudySession> findForUser(User user) {
		return studySessionRepository.findByUser(user);
	}

	public StudySession saveSession(User user, Subject subject, StudySessionDTO dto) {
		StudySession session = StudySession.builder()
				.user(user)
				.subject(subject)
				.durationMinutes(dto.getDurationMinutes())
				.date(dto.getDate())
				.startHour(dto.getStartHour())
				.build();
		return studySessionRepository.save(session);
	}

	public void deleteSession(User user, Long id) {
		StudySession session = studySessionRepository.findById(id)
				.filter(s -> s.getUser().getId().equals(user.getId()))
				.orElseThrow(() -> new IllegalArgumentException("Session introuvable"));
		studySessionRepository.delete(session);
	}

	public List<StudySession> findBetween(User user, LocalDate start, LocalDate end) {
		return studySessionRepository.findByUserAndDateBetween(user, start, end);
	}
}

