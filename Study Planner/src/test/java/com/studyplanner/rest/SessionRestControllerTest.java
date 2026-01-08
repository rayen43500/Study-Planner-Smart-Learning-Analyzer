package com.studyplanner.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyplanner.dto.StudySessionDTO;
import com.studyplanner.models.StudySession;
import com.studyplanner.models.Subject;
import com.studyplanner.models.User;
import com.studyplanner.services.StudySessionService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SessionRestController.class)
class SessionRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StudySessionService studySessionService;

	@MockBean
	private SubjectService subjectService;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	private User testUser;
	private Subject testSubject;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
				.id(1L)
				.username("testuser")
				.email("test@example.com")
				.password("password")
				.build();

		testSubject = Subject.builder()
				.id(1L)
				.name("Maths")
				.user(testUser)
				.build();

		when(userService.getCurrentUser()).thenReturn(testUser);
	}

	@Test
	@WithMockUser
	void listSessions_ShouldReturnList() throws Exception {
		// Given
		List<StudySession> sessions = Arrays.asList(
				StudySession.builder()
						.id(1L)
						.user(testUser)
						.subject(testSubject)
						.durationMinutes(60)
						.date(LocalDate.of(2025, 1, 15))
						.build()
		);
		when(studySessionService.findForUser(testUser)).thenReturn(sessions);

		// When/Then
		mockMvc.perform(get("/api/sessions")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].durationMinutes").value(60));

		verify(studySessionService).findForUser(testUser);
	}

	@Test
	@WithMockUser
	void getSession_WithValidId_ShouldReturnSession() throws Exception {
		// Given
		Long sessionId = 1L;
		StudySession session = StudySession.builder()
				.id(sessionId)
				.user(testUser)
				.subject(testSubject)
				.durationMinutes(60)
				.date(LocalDate.of(2025, 1, 15))
				.build();

		when(studySessionService.getOwnedSession(testUser, sessionId)).thenReturn(session);

		// When/Then
		mockMvc.perform(get("/api/sessions/{id}", sessionId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.durationMinutes").value(60));

		verify(studySessionService).getOwnedSession(testUser, sessionId);
	}

	@Test
	@WithMockUser
	void createSession_WithValidData_ShouldCreate() throws Exception {
		// Given
		StudySessionDTO dto = new StudySessionDTO();
		dto.setSubjectId(1L);
		dto.setDurationMinutes(60);
		dto.setDate(LocalDate.of(2025, 1, 15));
		dto.setStartHour(10);
		dto.setStartMinute(0);

		StudySession savedSession = StudySession.builder()
				.id(1L)
				.user(testUser)
				.subject(testSubject)
				.durationMinutes(60)
				.date(LocalDate.of(2025, 1, 15))
				.build();

		when(subjectService.getOwnedSubject(testUser, 1L)).thenReturn(testSubject);
		when(studySessionService.saveSession(any(User.class), any(Subject.class), any(StudySessionDTO.class)))
				.thenReturn(savedSession);

		// When/Then
		mockMvc.perform(post("/api/sessions")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.durationMinutes").value(60));

		verify(studySessionService).saveSession(testUser, testSubject, dto);
	}

	@Test
	@WithMockUser
	void deleteSession_WithValidId_ShouldDelete() throws Exception {
		// Given
		Long sessionId = 1L;
		doNothing().when(studySessionService).deleteSession(testUser, sessionId);

		// When/Then
		mockMvc.perform(delete("/api/sessions/{id}", sessionId)
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verify(studySessionService).deleteSession(testUser, sessionId);
	}
}
