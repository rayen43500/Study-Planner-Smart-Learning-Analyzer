package com.studyplanner.repositories;

import com.studyplanner.dto.StudySessionDTO;
import com.studyplanner.models.Subject;
import com.studyplanner.models.User;
import com.studyplanner.services.StudySessionService;
import com.studyplanner.services.SubjectService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@DataJpaTest
@Import({StudySessionService.class, SubjectService.class})
@ActiveProfiles("test")
class StudySessionRepositoryTest {

	@Autowired
	private StudySessionService studySessionService;

	@Autowired
	private SubjectService subjectService;

	@Autowired
	private UserRepository userRepository;

	@Test
	void persistsMultipleSessionsAndReadsThemBack() {
		User user = userRepository.save(User.builder()
				.username("planner")
				.email("planner@example.com")
				.password("secret")
				.build());

		Subject subject = subjectService.saveSubject(user, Subject.builder().name("TeamLife").build());

		createSession(user, subject, 50, LocalDate.of(2025, 12, 3), 10, 12);
		createSession(user, subject, 25, LocalDate.of(2025, 12, 4), 8, 0);

		var sessions = studySessionService.findForUser(user);
		Assertions.assertThat(sessions).hasSize(2);
		Assertions.assertThat(sessions)
				.allMatch(session -> session.getSubject().getName().equals("TeamLife"));
	}

	private void createSession(User user, Subject subject, int duration, LocalDate date, int hour, int minute) {
		StudySessionDTO dto = new StudySessionDTO();
		dto.setDurationMinutes(duration);
		dto.setDate(date);
		dto.setStartHour(hour);
		dto.setStartMinute(minute);
		studySessionService.saveSession(user, subject, dto);
	}
}

