package com.projects.bookhere.repository;

import com.projects.bookhere.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/* Database operation of authority */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {

}