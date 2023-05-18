package com.example.crud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.crud.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByPublished(boolean published);
    List<Course> findByTitleContaining(String title);
}
