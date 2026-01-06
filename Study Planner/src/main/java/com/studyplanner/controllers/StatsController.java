package com.studyplanner.controllers;

import com.studyplanner.services.StatsService;
import com.studyplanner.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

	private final StatsService statsService;
	private final UserService userService;

	@GetMapping
	public String stats(Model model) {
		var user = userService.getCurrentUser();
		
		Map<String, Integer> dailyStats = statsService.getDailyTotals(user, 7);
		model.addAttribute("dailyStats", dailyStats);
		model.addAttribute("dailyStatsKeys", new ArrayList<>(dailyStats.keySet()));
		model.addAttribute("dailyStatsValues", new ArrayList<>(dailyStats.values()));
		
		Map<String, Integer> weeklyStats = statsService.getWeeklyTotals(user, 8);
		model.addAttribute("weeklyStats", weeklyStats);
		model.addAttribute("weeklyStatsKeys", new ArrayList<>(weeklyStats.keySet()));
		model.addAttribute("weeklyStatsValues", new ArrayList<>(weeklyStats.values()));
		
		model.addAttribute("aiReport", statsService.buildAiReport(user));
		return "stats";
	}
}

