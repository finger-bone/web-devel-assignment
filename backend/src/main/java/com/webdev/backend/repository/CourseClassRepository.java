package com.webdev.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.webdev.backend.model.*;

public interface CourseClassRepository extends JpaRepository<CourseClass, Long>, JpaSpecificationExecutor<CourseClass> {

	CourseClass findByCourseIdAndClassId(Long courseId, Long classId);

	List<CourseClass> findByCourseId(Long courseId);

}