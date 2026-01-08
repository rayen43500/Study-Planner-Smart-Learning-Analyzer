package com.studyplanner.controllers;

import com.studyplanner.dto.StudySessionDTO;
import com.studyplanner.models.User;
import com.studyplanner.services.StudySessionService;
import com.studyplanner.services.SubjectService;
import com.studyplanner.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class StudySessionController {

	private final StudySessionService studySessionService;
	private final SubjectService subjectService;
	private final UserService userService;

	@GetMapping
	public String listSessions(Model model) {
		try {
			var user = userService.getCurrentUser();
			var sessions = studySessionService.findForUser(user);
			model.addAttribute("sessions", sessions != null ? sessions : List.of());
			return "sessions";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "Erreur lors du chargement des sessions : " + e.getMessage());
			model.addAttribute("sessions", List.of());
			return "sessions";
		}
	}

	@GetMapping("/add")
	public String addSession(Model model) {
		var user = userService.getCurrentUser();
		StudySessionDTO dto = new StudySessionDTO();
		dto.setDate(LocalDate.now());
		dto.setStartHour(9);
		dto.setStartMinute(0);
		populateSubjects(model, user, dto);
		return "add-session";
	}

	@PostMapping
	public String createSession(@Valid @ModelAttribute("session") StudySessionDTO dto,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes,
			HttpServletRequest request,
			@RequestParam(value = "stayOnPage", required = false) String stayOnPage) {
		var user = userService.getCurrentUser();
		ensureSubjectSelected(model, user, dto);
		if (bindingResult.hasErrors()) {
			// add subjects back for the form
			populateSubjects(model, user, dto);
			// log submitted parameters to help debug why subjectId may be empty
			var params = request.getParameterMap();
			System.out.println("[DEBUG] createSession binding errors. Request parameters:");
			params.forEach((k, v) -> System.out.println(k + " = " + String.join(",", v)));
			model.addAttribute("requestParams", params);
			return "add-session";
		}
		try {
			var subject = subjectService.getOwnedSubject(user, dto.getSubjectId());
			studySessionService.saveSession(user, subject, dto);
			redirectAttributes.addFlashAttribute("successMessage", "Session enregistrée avec succès.");
			if (stayOnPage != null) {
				return "redirect:/sessions/add";
			}
			return "redirect:/sessions";
		} catch (IllegalArgumentException e) {
			populateSubjects(model, user, dto);
			model.addAttribute("errorMessage", e.getMessage());
			return "add-session";
		}
	}

	@GetMapping("/delete/{id}")
	public String deleteSession(@PathVariable String id, RedirectAttributes redirectAttributes) {
		var user = userService.getCurrentUser();
		try {
			studySessionService.deleteSession(user, id);
			redirectAttributes.addFlashAttribute("successMessage", "Session supprimée avec succès.");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/sessions";
	}

	private void populateSubjects(Model model, User user, StudySessionDTO dto) {
		var subjects = subjectService.findForUser(user);
		if (!subjects.isEmpty() && (dto.getSubjectId() == null || dto.getSubjectId().isBlank())) {
			dto.setSubjectId(subjects.get(0).getId());
		}
		model.addAttribute("session", dto);
		model.addAttribute("subjects", subjects);
		model.addAttribute("hasSubjects", !subjects.isEmpty());
	}

	private void ensureSubjectSelected(Model model, User user, StudySessionDTO dto) {
		var subjects = subjectService.findForUser(user);
		if (!StringUtils.hasText(dto.getSubjectId()) && !subjects.isEmpty()) {
			dto.setSubjectId(subjects.get(0).getId());
		}
		if (subjects.isEmpty()) {
			model.addAttribute("subjects", subjects);
			model.addAttribute("hasSubjects", false);
			throw new IllegalArgumentException("Veuillez créer une matière avant de planifier une session.");
		}
	}
}

