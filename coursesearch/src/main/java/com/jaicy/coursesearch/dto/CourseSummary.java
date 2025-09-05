package com.jaicy.coursesearch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseSummary {
	private String id;
	private String title;
	private String category;
	private String type;
	private Double price;
	private String nextSessionDate;

}
