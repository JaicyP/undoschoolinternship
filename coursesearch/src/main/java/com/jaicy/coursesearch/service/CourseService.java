package com.jaicy.coursesearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.stereotype.Service;

import com.jaicy.coursesearch.dto.CourseSummary;
import com.jaicy.coursesearch.entity.Course;
import com.jaicy.coursesearch.entity.CourseType;
import com.jaicy.coursesearch.repository.CourseRepository;

import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class CourseService {
    
	@Autowired
	private CourseRepository courseRepository;
	
	private final ElasticsearchOperations operations;
	
	public Iterable<Course> getAll() {
		return courseRepository.findAll();
	}
    public Page<Course> search(String q,
                           Integer minAge,
                           Integer maxAge,
                           String category,
                           CourseType type,
                           Double minPrice,
                           Double maxPrice,
                           Instant startDate,
                           String sort,
                           int page,
                           int size) {

	  // --- Base (full-text OR match_all) ---
    Criteria criteria;
    if (q != null && !q.isBlank()) {
        Criteria title = new Criteria("title").matches(q).boost(3.0f);
        Criteria description = new Criteria("description").matches(q);
        criteria = title.or(description);                 // has fields
    } else {
        criteria = new Criteria("_id").exists();          // match_all equivalent, has field
    }

    // --- Filters (append only when present; each has a field) ---
    if (minAge != null)        criteria = criteria.and("minAge").greaterThanEqual(minAge);
    if (maxAge != null)        criteria = criteria.and("maxAge").lessThanEqual(maxAge);
 
    if (category != null && !category.isBlank())
                               criteria = criteria.and("category").is(category);
    if (type != null)          criteria = criteria.and("type").is(type.name());
    if (minPrice != null)      criteria = criteria.and("price").greaterThanEqual(minPrice);
    if (maxPrice != null)      criteria = criteria.and("price").lessThanEqual(maxPrice);
    if (startDate != null)     criteria = criteria.and("nextSessionDate").greaterThanEqual(startDate);

    // --- Sort & paging ---
    Sort springSort = switch (sort == null ? "upcoming" : sort) {
        case "priceAsc"  -> Sort.by(Sort.Order.asc("price"));
        case "priceDesc" -> Sort.by(Sort.Order.desc("price"));
        default          -> Sort.by(Sort.Order.asc("nextSessionDate"));
    };
    Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), springSort);

    // --- Execute ---
    Query query = new CriteriaQuery(criteria, pageable);
    SearchHits<Course> hits = operations.search(query, Course.class);
    return SearchHitSupport.searchPageFor(hits, pageable).map(SearchHit::getContent);
 }

   public List<String> suggest(String prefix){
	  if (prefix == null || prefix.isBlank()) return List.of();

	    Suggester suggester = Suggester.of(s -> s
	        .suggesters("title-suggest", sg -> sg
	            .prefix(prefix)  
	            .completion(CompletionSuggester.of(cs -> cs
	                .field("titleSuggest")
	                .fuzzy(f -> f.fuzziness("AUTO"))
	                .skipDuplicates(true)
	                .size(10)
	            ))
	        )
	    );

	    NativeQuery query = NativeQuery.builder()
	        .withSuggester(suggester)
	        .build();

	    SearchHits<Course> hits = operations.search(query, Course.class);

	    Suggest suggest = hits.getSuggest();
	    if (suggest == null) return List.of();

	    return suggest.getSuggestion("title-suggest").getEntries().stream()
	        .flatMap(e -> e.getOptions().stream())
	        .map(o -> o.getText().toString())
	        .distinct()
	        .limit(10)
	        .collect(Collectors.toList());
	    
    }

    public static CourseSummary courseSummary(Course c) {
	     return CourseSummary.builder()
			   .id(c.getId())
			   .title(c.getTitle())
			   .category(c.getCategory())
			   .type(c.getType()!=null?
					   c.getType().name():null)
			   .price(c.getPrice())
			   .nextSessionDate(c.getNextSessionDate()!=null?
					   c.getNextSessionDate().toString():null)
			   .build();
  }
  }
