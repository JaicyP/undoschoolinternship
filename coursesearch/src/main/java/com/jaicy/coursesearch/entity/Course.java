package com.jaicy.coursesearch.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Mapping(mappingPath = "/es/course-mappings.json")
@Document(indexName="courses")
public class Course {
	@Id
	private String id;
	
	@Field(type = FieldType.Text)
	private String title;
	
	@Field(type = FieldType.Text)
	private String description;
	
	@Field(type = FieldType.Keyword)
	private String category;
	
	@Field(type = FieldType.Keyword)
	private CourseType type;
	
	@Field(type = FieldType.Keyword)
	private String gradeRange;
	
	@Field(type = FieldType.Integer)
	private Integer minAge;
	
	@Field(type = FieldType.Integer)
	private Integer maxAge;
	
	@Field(type = FieldType.Double)
	private Double price;
	
	@Field(type = FieldType.Date, format = DateFormat.date_time)
	private Instant nextSessionDate;
	
	@CompletionField(maxInputLength=100)
	private Completion titleSuggest;

}
