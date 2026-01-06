package com.studyplanner.controllers;

import com.studyplanner.models.Subject;
import com.studyplanner.services.SubjectService;
import com.studyplanner.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

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
	public String createSubject(@Valid @ModelAttribute Subject subject,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "add-subject";
		}
		try {
			var user = userService.getCurrentUser();
			subjectService.saveSubject(user, subject);
			redirectAttributes.addFlashAttribute("successMessage", "Matière enregistrée avec succès.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'enregistrement : " + e.getMessage());
			return "redirect:/subjects/add";
		}
		return "redirect:/subjects";
	}

	@GetMapping("/edit/{id}")
	public String editSubject(@PathVariable String id, Model model) {
		var user = userService.getCurrentUser();
		model.addAttribute("subject", subjectService.getOwnedSubject(user, id));
		return "add-subject";
	}

	@PostMapping("/update")
	public String updateSubject(@Valid @ModelAttribute Subject subject,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "add-subject";
		}
		try {
			var user = userService.getCurrentUser();
			subjectService.saveSubject(user, subject);
			redirectAttributes.addFlashAttribute("successMessage", "Matière mise à jour avec succès.");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/subjects/edit/" + subject.getId();
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour : " + e.getMessage());
			return "redirect:/subjects";
		}
		return "redirect:/subjects";
	}

	@GetMapping("/delete/{id}")
	public String deleteSubject(@PathVariable String id, RedirectAttributes redirectAttributes) {
		var user = userService.getCurrentUser();
		try {
			subjectService.deleteSubject(user, id);
			redirectAttributes.addFlashAttribute("successMessage", "Matière supprimée avec succès.");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/subjects";
	}

	@PostMapping("/quick-add")
	public String quickAddSubjects(@RequestParam("names") String names,
			RedirectAttributes redirectAttributes) {
		var user = userService.getCurrentUser();
		String sanitized = names == null ? "" : names;
		List<String> parsedNames = Arrays.stream(sanitized.split("[,;\\r\\n]+"))
				.map(String::trim)
				.filter(StringUtils::hasText)
				.toList();

		if (parsedNames.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Veuillez saisir au moins un nom de matière.");
			return "redirect:/subjects";
		}

		var created = subjectService.saveSubjects(user, parsedNames);
		if (created.isEmpty()) {
			redirectAttributes.addFlashAttribute("warningMessage", "Aucune nouvelle matière ajoutée (doublons ignorés).");
		} else {
			redirectAttributes.addFlashAttribute("successMessage",
					created.size() + " matière(s) ajoutée(s) avec succès.");
		}
		return "redirect:/subjects";
	}
}

