package com.studyplanner.repositories;

import com.studyplanner.models.Subject;
import com.studyplanner.models.User;
import com.studyplanner.services.SubjectService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@DataMongoTest
@Import(SubjectService.class)
class SubjectRepositoryTest {

	@Autowired
	private SubjectService subjectService;

	@Autowired
	private UserRepository userRepository;

	@Test
	void savesMultipleSubjectsForSameUserInMongo() {
		User user = userRepository.save(User.builder()
				.username("teamlife")
				.email("teamlife@example.com")
				.password("secret")
				.build());

		var saved = subjectService.saveSubjects(user, List.of("Maths", "Physique", "Maths"));

		Assertions.assertThat(saved).hasSize(2);

		var reloaded = subjectService.findForUser(user);
		Assertions.assertThat(reloaded)
				.extracting(Subject::getName)
				.containsExactlyInAnyOrder("Maths", "Physique");
	}
}

