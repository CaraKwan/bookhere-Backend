package com.projects.bookhere.repository;

import java.util.List;

/* Declare searchByDistance in an extra interface, avoiding implementation of all methods that
   LocationRepository inherits from ElasticsearchRepository */
public interface CustomLocationRepository {
    //Return a list of id of stays that are within a distance from a location
    List<Long> searchByDistance(double lat, double lon, String distance);
}
