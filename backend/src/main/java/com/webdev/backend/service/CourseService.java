package com.webdev.backend.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webdev.backend.model.Course;
import com.webdev.backend.model.CourseClass;
import com.webdev.backend.model.CourseTeacher;
import com.webdev.backend.model.Employee;
import com.webdev.backend.repository.ClassRepository;
import com.webdev.backend.repository.CourseClassRepository;
import com.webdev.backend.repository.CourseRepository;
import com.webdev.backend.repository.CourseTeacherRepository;
import com.webdev.backend.repository.EmployeeRepository;
import com.webdev.backend.model.Class;

import jakarta.persistence.criteria.Predicate;

@Service
public class CourseService {

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private CourseClassRepository courseClassRepository;

	@Autowired
	private CourseTeacherRepository courseTeacherRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ClassRepository classRepository;

	public void createCourse(Course course) {
		courseRepository.save(course);
	}

	public void updateCourse(Course course) {
		courseRepository.save(course);
	}

	public void deleteCourse(Long id) {
		courseRepository.deleteById(id);
	}

	public void addTeacherToCourse(Long courseId, Long teacherId) {
		CourseTeacher courseTeacher = new CourseTeacher();
		courseTeacher.setCourseId(courseId);
		courseTeacher.setTeacherId(teacherId);
		courseTeacherRepository.save(courseTeacher);
	}

	public void addClassToCourse(Long courseId, Long classId) {
		CourseClass courseClass = new CourseClass();
		courseClass.setCourseId(courseId);
		courseClass.setClassId(classId);
		courseClassRepository.save(courseClass);
	}

	public void removeClassFromCourse(Long courseId, Long classId) {
		CourseClass courseClass = courseClassRepository.findByCourseIdAndClassId(courseId, classId);
		if (courseClass != null) {
			courseClassRepository.delete(courseClass);
		}
	}

	public void removeTeacherFromCourse(Long courseId, Long teacherId) {
		CourseTeacher courseTeacher = courseTeacherRepository.findByCourseIdAndTeacherId(courseId, teacherId);
		if (courseTeacher != null) {
			courseTeacherRepository.delete(courseTeacher);
		}
	}

	public List<Course> searchCourses(String courseName, Integer courseCredit, Date endTimeStart, Date endTimeEnd,
			Long start, Long end) {
		List<Course> courses = courseRepository.findAll((root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (courseName != null) {
				predicates.add(cb.like(root.get("courseName"), "%" + courseName + "%"));
			}
			if (courseCredit != null) {
				predicates.add(cb.equal(root.get("courseCredit"), courseCredit));
			}
			if (endTimeStart != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("endTime"), endTimeStart));
			}
			if (endTimeEnd != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("endTime"), endTimeEnd));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		});
		return courses.subList(Math.min(start.intValue(), courses.size()), Math.min(end.intValue(), courses.size()));
	}

	public List<Employee> getTeachers(Long courseId) {
		List<CourseTeacher> courseTeachers = courseTeacherRepository.findByCourseId(courseId);
		List<Long> teacherIds = courseTeachers.stream().map(CourseTeacher::getTeacherId).collect(Collectors.toList());
		return employeeRepository.findAllById(teacherIds);
	}

	public List<Class> getClasses(Long courseId) {
		List<CourseClass> courseClasses = courseClassRepository.findByCourseId(courseId);
		List<Long> classIds = courseClasses.stream().map(CourseClass::getClassId).collect(Collectors.toList());
		return classRepository.findAllById(classIds);
	}

}