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

import java.util.ArrayList;
import java.util.Map;

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
		
		Map<String, Integer> dailyStats = statsService.getDailyTotals(user, 7);
		model.addAttribute("dailyStats", dailyStats);
		model.addAttribute("dailyStatsKeys", new ArrayList<>(dailyStats.keySet()));
		model.addAttribute("dailyStatsValues", new ArrayList<>(dailyStats.values()));
		
		model.addAttribute("aiReport", statsService.buildAiReport(user));
		return "dashboard";
	}
}

