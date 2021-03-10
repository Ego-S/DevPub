package com.devpub.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.TreeMap;
import java.util.TreeSet;

@Data
@AllArgsConstructor
public class CalendarDTO {
	private TreeSet<Integer> years;
	@JsonProperty("posts")
	private TreeMap<LocalDate, Integer> dateToPostCount;
}
