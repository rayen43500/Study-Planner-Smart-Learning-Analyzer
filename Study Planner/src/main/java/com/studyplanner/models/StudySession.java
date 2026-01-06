package com.studyplanner.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "study_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySession {

	@Id
	private String id;

	@DBRef
	private Subject subject;

	@DBRef
	@JsonIgnore
	private User user;

	private int durationMinutes;

	private LocalDate date;
	private Integer startHour;

	private Integer startMinute;
}

