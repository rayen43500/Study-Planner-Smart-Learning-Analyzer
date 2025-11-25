package com.studyplanner.controllers;

import com.studyplanner.dto.SignupRequest;
import com.studyplanner.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthViewController {

	private final UserService userService;

	@GetMapping("/")
	public String homeRedirect() {
		return "redirect:/dashboard";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register(@ModelAttribute("user") SignupRequest request) {
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(@ModelAttribute("user") SignupRequest request, RedirectAttributes redirectAttributes) {
		try {
			userService.registerUser(request, false);
			redirectAttributes.addFlashAttribute("success", "Compte créé. Connectez-vous !");
			return "redirect:/login";
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("error", ex.getMessage());
			return "redirect:/register";
		}
	}
}

