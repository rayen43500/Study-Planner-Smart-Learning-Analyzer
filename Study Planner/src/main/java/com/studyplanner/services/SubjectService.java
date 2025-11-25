package com.studyplanner.services;

import com.studyplanner.models.Subject;
import com.studyplanner.models.User;
import com.studyplanner.repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

	private final SubjectRepository subjectRepository;

	public List<Subject> findForUser(User user) {
		return subjectRepository.findByUser(user);
	}

	public Subject saveSubject(User user, Subject subject) {
		subject.setUser(user);
		return subjectRepository.save(subject);
	}

	public Subject getOwnedSubject(User user, Long id) {
		return subjectRepository.findById(id)
				.filter(subject -> subject.getUser().getId().equals(user.getId()))
				.orElseThrow(() -> new IllegalArgumentException("Matière introuvable"));
	}

	public void deleteSubject(User user, Long id) {
		Subject subject = getOwnedSubject(user, id);
		subjectRepository.delete(subject);
	}
}

