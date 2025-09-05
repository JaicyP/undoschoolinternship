package com.jaicy.coursesearch.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.jaicy.coursesearch.entity.Course;
@Repository
public interface CourseRepository extends ElasticsearchRepository<Course,String> {

   

}
