package com.webdev.backend.repository;

import com.webdev.backend.model.Class;
import com.webdev.backend.model.Course;
import com.webdev.backend.model.CourseClass;
import com.webdev.backend.model.CourseTeacher;
import com.webdev.backend.model.Employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

}