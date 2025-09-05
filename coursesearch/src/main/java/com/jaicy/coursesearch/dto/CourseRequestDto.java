package com.jaicy.coursesearch.dto;

import java.time.Instant;

import org.springframework.format.annotation.DateTimeFormat;

import com.jaicy.coursesearch.entity.CourseType;

import lombok.Data;

@Data
public class CourseRequestDto {
	
	private String q;
	private Integer minAge;
	private Integer maxAge;
	private String category;
	private CourseType type;
	private Double minPrice;
	private Double maxPrice;
	
	@DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
	private Instant startDate;
	
	private String sort;
	
	private Integer page=0;
	private Integer size=10;

}
