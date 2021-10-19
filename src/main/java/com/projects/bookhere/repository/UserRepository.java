package com.projects.bookhere.repository;

import com.projects.bookhere.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/* Database operation of user */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
//JpaRepository<model type, id type>
}