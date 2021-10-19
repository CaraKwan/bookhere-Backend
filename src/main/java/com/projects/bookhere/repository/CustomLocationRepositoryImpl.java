package com.projects.bookhere.repository;

import com.projects.bookhere.model.Location;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;

/* Implementation of searchByDistance method */
public class CustomLocationRepositoryImpl implements CustomLocationRepository {
    private final String DEFAULT_DISTANCE = "50";
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public CustomLocationRepositoryImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    //Return a list of id of stays that are within a distance from a location
    @Override
    public List<Long> searchByDistance(double lat, double lon, String distance) {
        if (distance == null || distance.isEmpty()) {
            distance = DEFAULT_DISTANCE;
        }
        //Define a query and use elasticsearchOperations to execute the query
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withFilter(new GeoDistanceQueryBuilder("geoPoint").point(lat, lon).distance(distance, DistanceUnit.KILOMETERS));

        SearchHits<Location> searchResult = elasticsearchOperations.search(queryBuilder.build(), Location.class);
        List<Long> locationIDs = new ArrayList<>();
        for (SearchHit<Location> hit : searchResult.getSearchHits()) {
            locationIDs.add(hit.getContent().getId());
        }
        return locationIDs;
    }
}