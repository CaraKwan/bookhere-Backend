package com.projects.bookhere.repository;

import com.projects.bookhere.model.Location;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/* Database operation of location */
@Repository
public interface LocationRepository extends ElasticsearchRepository<Location, Long>, CustomLocationRepository {

}