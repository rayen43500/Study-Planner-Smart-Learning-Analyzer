package com.studyplanner.controllers;

import com.studyplanner.services.StatsService;
import com.studyplanner.services.StudySessionService;
import com.studyplanner.services.SubjectService;
import com.studyplanner.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

	private final UserService userService;
	private final SubjectService subjectService;
	private final StudySessionService studySessionService;
	private final StatsService statsService;

	@GetMapping
	public String dashboard(Model model) {
		var user = userService.getCurrentUser();
		model.addAttribute("subjectCount", subjectService.findForUser(user).size());
		model.addAttribute("sessionCount", studySessionService.findForUser(user).size());
		model.addAttribute("dailyStats", statsService.getDailyTotals(user, 7));
		model.addAttribute("aiReport", statsService.buildAiReport(user));
		return "dashboard";
	}
}

