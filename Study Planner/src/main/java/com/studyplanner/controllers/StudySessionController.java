package com.studyplanner.controllers;

import com.studyplanner.dto.StudySessionDTO;
import com.studyplanner.services.StudySessionService;
import com.studyplanner.services.SubjectService;
import com.studyplanner.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class StudySessionController {

	private final StudySessionService studySessionService;
	private final SubjectService subjectService;
	private final UserService userService;

	@GetMapping
	public String listSessions(Model model) {
		var user = userService.getCurrentUser();
		model.addAttribute("sessions", studySessionService.findForUser(user));
		return "sessions";
	}

	@GetMapping("/add")
	public String addSession(Model model) {
		var user = userService.getCurrentUser();
		StudySessionDTO dto = new StudySessionDTO();
		dto.setDate(LocalDate.now());
		model.addAttribute("session", dto);
		model.addAttribute("subjects", subjectService.findForUser(user));
		return "add-session";
	}

	@PostMapping
	public String createSession(@Valid @ModelAttribute("session") StudySessionDTO dto, BindingResult bindingResult, Model model) {
		var user = userService.getCurrentUser();
		if (bindingResult.hasErrors()) {
			model.addAttribute("subjects", subjectService.findForUser(user));
			return "add-session";
		}
		var subject = subjectService.getOwnedSubject(user, dto.getSubjectId());
		studySessionService.saveSession(user, subject, dto);
		return "redirect:/sessions";
	}

	@GetMapping("/delete/{id}")
	public String deleteSession(@PathVariable Long id) {
		var user = userService.getCurrentUser();
		studySessionService.deleteSession(user, id);
		return "redirect:/sessions";
	}
}

