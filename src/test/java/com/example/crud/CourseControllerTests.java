package com.example.crud;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.crud.controller.CourseController;
import com.example.crud.model.Course;
import com.example.crud.repository.CourseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CourseController.class)
public class CourseControllerTests {
    @MockBean
    private CourseRepository courseRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCourse() throws Exception {
        Course course = new Course(1, "Create course test", "Description", true);

        mockMvc.perform(post("/api/courses").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    void shouldReturnCourse() throws Exception {
        long id = 1L;
        Course course= new Course(1, "Return course test", "Description", true);

        when(courseRepository.findById(id)).thenReturn(Optional.of(course));
        mockMvc.perform(get("/api/courses/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value(course.getTitle()))
                .andExpect(jsonPath("$.description").value(course.getDescription()))
                .andExpect(jsonPath("$.published").value(course.isPublished()))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundCourse() throws Exception {
        long id = 1L;

        when(courseRepository.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/courses/{id}", id))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void shouldReturnListOfCourse() throws Exception {
        List<Course> courses = new ArrayList<>(
                Arrays.asList(new Course(1, "Course test 001", "Description 1", true),
                    new Course(2, "Course test 002", "Description 2", true),
                       new Course(3, "Course teste 003", "Description 3", true)));

        when(courseRepository.findAll()).thenReturn(courses);
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(courses.size()))
                .andDo(print());
    }

    @Test
    void shouldReturnListOfCoursesWithFilter() throws Exception {
        List<Course> courses = new ArrayList<>(
                Arrays.asList(new Course(1, "Couser teste with filter 001", "Description 1", true),
                    new Course(3, "Couser teste with filter 002", "Description 2", true),
                    new Course(3, "Couser teste with filter 003", "Description 3", true)));

        String title = "Course";
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add("title", title);

        when(courseRepository.findByTitleContaining(title)).thenReturn(courses);
        mockMvc.perform(get("/api/courses").params(paramsMap))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(courses.size()))
                .andDo(print());

        courses = Collections.emptyList();

        when(courseRepository.findByTitleContaining(title)).thenReturn(courses);
        mockMvc.perform(get("/api/courses").params(paramsMap))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void shouldReturnNoCourseWhenFilter() throws Exception {
        String title = "Test course ";
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add("title", title);

        List<Course> course = Collections.emptyList();

        when(courseRepository.findByTitleContaining(title)).thenReturn(course);
        mockMvc.perform(get("/api/courses").params(paramsMap))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void shouldUpdateCourse() throws Exception {
        long id = 1L;

        Course course = new Course(id, "Test update course", "Description", false);
        Course updatedCourse = new Course(id, "Course updated", "Description updated", true);

        when(courseRepository.findById(id)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(updatedCourse);

        mockMvc.perform(put("/api/courses/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCourse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updatedCourse.getTitle()))
                .andExpect(jsonPath("$.description").value(updatedCourse.getDescription()))
                .andExpect(jsonPath("$.published").value(updatedCourse.isPublished()))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundUpdateCourse() throws Exception {
        long id = 1L;

        Course updatedCourse = new Course(id, "Title course updated", " Description course pdated", true);

        when(courseRepository.findById(id)).thenReturn(Optional.empty());
        when(courseRepository.save(any(Course.class))).thenReturn(updatedCourse);

        mockMvc.perform(put("/api/courses/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCourse)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void shouldDeleteCourse() throws Exception {
        long id = 1L;

        doNothing().when(courseRepository).deleteById(id);
        mockMvc.perform(delete("/api/courses/{id}", id))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void shouldDeleteAllCourses() throws Exception {
        doNothing().when(courseRepository).deleteAll();
        mockMvc.perform(delete("/api/courses"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}