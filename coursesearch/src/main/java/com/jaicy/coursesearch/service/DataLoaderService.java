package com.jaicy.coursesearch.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaicy.coursesearch.entity.Course;
import com.jaicy.coursesearch.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoaderService implements CommandLineRunner{
	
	    private final CourseRepository repo;
	    private final ElasticsearchOperations operations;
	    private final ObjectMapper objectMapper;

	    @Override
	    public void run(String... args) throws Exception {
	        var indexOps = operations.indexOps(Course.class);
	        if (!indexOps.exists()) {
	            indexOps.create();
	            indexOps.putMapping(indexOps.createMapping()); // Boot 3.3.x style
	        }

	        if (repo.count() == 0) {
	            var resource = new ClassPathResource("courses.json");
	            try (InputStream is = resource.getInputStream()) {
	                List<Course> courses = objectMapper.readValue(is, new TypeReference<>() {});

	                // IMPORTANT: set completion inputs from title (and optionally category)
	                for (Course c : courses) {
	                    c.setTitleSuggest(new Completion(new String[] { c.getTitle() }));
	                }

	                repo.saveAll(courses);
	                operations.indexOps(Course.class).refresh();
	                System.out.println("Indexed " + courses.size() + " courses.");
	            }
	        }
	    }

}
