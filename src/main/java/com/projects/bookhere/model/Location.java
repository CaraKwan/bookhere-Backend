package com.projects.bookhere.model;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.io.Serializable;

/* Store data in Elasticsearch instead of Mysql */
//There is only one table(type) in a database(index) in elasticsearch
@Document(indexName = "loc")
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    @Field(type = FieldType.Long)
    private Long id;     //Stay id

    @GeoPointField
    private GeoPoint geoPoint;  //Latitude and longitude

    public Location(Long id, GeoPoint geoPoint) {
        this.id = id;
        this.geoPoint = geoPoint;
    }

    public Long getId() {
        return id;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }
}