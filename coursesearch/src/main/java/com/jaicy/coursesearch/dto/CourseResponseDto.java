package com.jaicy.coursesearch.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseResponseDto {
	
	private long total;
	private int page;
	private int size;
	private List<CourseSummary> courses;

}
