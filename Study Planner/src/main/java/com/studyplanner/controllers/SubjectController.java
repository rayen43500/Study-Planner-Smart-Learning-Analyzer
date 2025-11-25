package com.studyplanner.controllers;

import com.studyplanner.models.Subject;
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

@Controller
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

	private final SubjectService subjectService;
	private final UserService userService;

	@GetMapping
	public String listSubjects(Model model) {
		var user = userService.getCurrentUser();
		model.addAttribute("subjects", subjectService.findForUser(user));
		return "subjects";
	}

	@GetMapping("/add")
	public String addSubject(Model model) {
		model.addAttribute("subject", new Subject());
		return "add-subject";
	}

	@PostMapping
	public String createSubject(@Valid @ModelAttribute Subject subject, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "add-subject";
		}
		var user = userService.getCurrentUser();
		subjectService.saveSubject(user, subject);
		return "redirect:/subjects";
	}

	@GetMapping("/edit/{id}")
	public String editSubject(@PathVariable String id, Model model) {
		var user = userService.getCurrentUser();
		model.addAttribute("subject", subjectService.getOwnedSubject(user, id));
		return "add-subject";
	}

	@PostMapping("/update")
	public String updateSubject(@Valid @ModelAttribute Subject subject, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "add-subject";
		}
		var user = userService.getCurrentUser();
		subjectService.saveSubject(user, subject);
		return "redirect:/subjects";
	}

	@GetMapping("/delete/{id}")
	public String deleteSubject(@PathVariable String id) {
		var user = userService.getCurrentUser();
		subjectService.deleteSubject(user, id);
		return "redirect:/subjects";
	}
}

