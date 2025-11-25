package com.studyplanner.controllers;

import com.studyplanner.services.StatsService;
import com.studyplanner.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

	private final StatsService statsService;
	private final UserService userService;

	@GetMapping
	public String stats(Model model) {
		var user = userService.getCurrentUser();
		model.addAttribute("dailyStats", statsService.getDailyTotals(user, 7));
		model.addAttribute("weeklyStats", statsService.getWeeklyTotals(user, 4));
		model.addAttribute("aiReport", statsService.buildAiReport(user));
		return "stats";
	}
}

