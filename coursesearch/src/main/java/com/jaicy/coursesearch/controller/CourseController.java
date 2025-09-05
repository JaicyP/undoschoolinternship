package com.jaicy.coursesearch.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jaicy.coursesearch.dto.CourseRequestDto;
import com.jaicy.coursesearch.dto.CourseResponseDto;
import com.jaicy.coursesearch.dto.CourseSummary;
import com.jaicy.coursesearch.entity.Course;
import com.jaicy.coursesearch.service.CourseService;
import com.jaicy.coursesearch.service.IndexService;

@RestController
@RequestMapping("/api")
public class CourseController {
	@Autowired
	private IndexService service;
	
	@Autowired
	private CourseService courseService;
	
	
	@GetMapping
	public String test() {
		return "Testing the Application";
	}
	
	@GetMapping("/search")
	public CourseResponseDto search(CourseRequestDto req) {
		Page<Course> page=courseService.search(req.getQ(),
				             req.getMaxAge(),
				             req.getMaxAge(),
				             req.getCategory(),
				             req.getType(),
				             req.getMinPrice(),
				             req.getMaxPrice(),
				             req.getStartDate(),
				             req.getSort(),
				             req.getPage()==null?0:req.getPage(),
				             req.getSize()==null?0:req.getSize());
	           List<CourseSummary> courses= page.getContent().stream()
			                        .map(CourseService::courseSummary)
			                        .collect(Collectors.toList());	
	           return CourseResponseDto.builder()
	        		   .total(page.getTotalElements())
	        		   .page(page.getNumber())
	        		   .size(page.getSize())
	        		   .courses(courses)
	        		   .build();
				
	}
	
	
	@GetMapping("/suggest")
	public List<String> suggest(@RequestParam("q") String q){
		return courseService.suggest(q);
	}
	
	
	
	
	// Re-index from the JSON file in resources 
    @PostMapping("/reindex")
    public List<Course> reindex() throws Exception {
        return service.reindexFromResource();
    }

}
