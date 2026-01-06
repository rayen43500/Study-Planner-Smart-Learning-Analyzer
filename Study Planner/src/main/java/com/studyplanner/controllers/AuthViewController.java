package com.studyplanner.controllers;

import com.studyplanner.dto.SignupRequest;
import com.studyplanner.models.User;
import com.studyplanner.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
	public String registerUser(@Valid @ModelAttribute("user") SignupRequest request, 
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		try {
			System.out.println("[DEBUG] AuthViewController: Tentative de création d'utilisateur");
			System.out.println("[DEBUG] Username: " + (request.getUsername() != null ? request.getUsername() : "NULL"));
			System.out.println("[DEBUG] Email: " + (request.getEmail() != null ? request.getEmail() : "NULL"));
			
			// Vérifier les erreurs de validation
			if (bindingResult.hasErrors()) {
				System.out.println("[DEBUG] AuthViewController: Erreurs de validation détectées");
				bindingResult.getAllErrors().forEach(error -> 
					System.out.println("[DEBUG] Erreur: " + error.getDefaultMessage())
				);
				redirectAttributes.addFlashAttribute("error", "Veuillez corriger les erreurs du formulaire.");
				return "redirect:/register";
			}
			
			User savedUser = userService.registerUser(request, false);
			
			System.out.println("[DEBUG] AuthViewController: Utilisateur créé avec succès, ID: " + savedUser.getId());
			System.out.println("[DEBUG] AuthViewController: Redirection vers /login (302)");
			redirectAttributes.addFlashAttribute("success", "Compte créé avec succès. Connectez-vous !");
			return "redirect:/login";
		} catch (IllegalArgumentException ex) {
			System.out.println("[DEBUG] AuthViewController: Erreur - " + ex.getMessage());
			redirectAttributes.addFlashAttribute("error", ex.getMessage());
			return "redirect:/register";
		} catch (Exception ex) {
			System.out.println("[DEBUG] AuthViewController: Exception inattendue - " + ex.getMessage());
			ex.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "Erreur lors de la création du compte : " + ex.getMessage());
			return "redirect:/register";
		}
	}
}

