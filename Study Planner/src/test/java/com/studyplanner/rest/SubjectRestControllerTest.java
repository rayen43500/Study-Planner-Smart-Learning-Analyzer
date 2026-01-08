package com.studyplanner.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyplanner.models.Subject;
import com.studyplanner.models.User;
import com.studyplanner.services.SubjectService;
import com.studyplanner.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubjectRestController.class)
class SubjectRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SubjectService subjectService;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
				.id("user1")
				.username("testuser")
				.email("test@example.com")
				.password("password")
				.build();

		when(userService.getCurrentUser()).thenReturn(testUser);
	}

	@Test
	@WithMockUser
	void listSubjects_ShouldReturnList() throws Exception {
		// Given
		List<Subject> subjects = Arrays.asList(
				Subject.builder().id("sub1").name("Maths").user(testUser).build(),
				Subject.builder().id("sub2").name("Physics").user(testUser).build()
		);
		when(subjectService.findForUser(testUser)).thenReturn(subjects);

		// When/Then
		mockMvc.perform(get("/api/subjects")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].name").value("Maths"))
				.andExpect(jsonPath("$[1].name").value("Physics"));

		verify(subjectService).findForUser(testUser);
	}

	@Test
	@WithMockUser
	void createSubject_WithValidData_ShouldCreate() throws Exception {
		// Given
		Subject subject = Subject.builder()
				.name("Maths")
				.build();
		Subject savedSubject = Subject.builder()
				.id("sub1")
				.name("Maths")
				.user(testUser)
				.build();

		when(subjectService.saveSubject(any(User.class), any(Subject.class)))
				.thenReturn(savedSubject);

		// When/Then
		mockMvc.perform(post("/api/subjects")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(subject)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("sub1"))
				.andExpect(jsonPath("$.name").value("Maths"));

		verify(subjectService).saveSubject(testUser, subject);
	}

	@Test
	@WithMockUser
	void deleteSubject_WithValidId_ShouldDelete() throws Exception {
		// Given
		String subjectId = "sub1";
		doNothing().when(subjectService).deleteSubject(testUser, subjectId);

		// When/Then
		mockMvc.perform(delete("/api/subjects/{id}", subjectId)
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verify(subjectService).deleteSubject(testUser, subjectId);
	}
}
