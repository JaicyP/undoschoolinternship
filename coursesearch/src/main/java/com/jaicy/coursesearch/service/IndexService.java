package com.jaicy.coursesearch.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaicy.coursesearch.entity.Course;
import com.jaicy.coursesearch.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@Service
public class IndexService {
	    
	    private  CourseRepository repo;
	    private  ObjectMapper mapper;
	    
	    @Autowired
	    public IndexService(CourseRepository repo, ObjectMapper mapper) {
	        this.repo = repo;
	        this.mapper = mapper;
	    }

	
	public List<Course> reindexFromResource() throws Exception {
        ClassPathResource resource = new ClassPathResource("courses.json");
        try (InputStream is = resource.getInputStream()) {
            List<Course> products = mapper.readValue(is, new TypeReference<>() {});
            repo.deleteAll();
            repo.saveAll(products);
            return products;
        }
    }

}
