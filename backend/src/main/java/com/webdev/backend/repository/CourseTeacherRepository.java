package com.webdev.backend.repository;

import com.webdev.backend.model.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseTeacherRepository
		extends JpaRepository<CourseTeacher, Long>, JpaSpecificationExecutor<CourseTeacher> {

	CourseTeacher findByCourseIdAndTeacherId(Long courseId, Long teacherId);

	List<CourseTeacher> findByCourseId(Long courseId);

    List<CourseTeacher> findByTeacherId(Long teacherId);

}
