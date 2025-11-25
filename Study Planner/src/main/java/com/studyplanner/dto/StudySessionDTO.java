package com.studyplanner.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudySessionDTO {

	@NotNull
	private Long subjectId;

	@Min(1)
	private int durationMinutes;

	@NotNull
	private LocalDate date;

	private Integer startHour;
}

