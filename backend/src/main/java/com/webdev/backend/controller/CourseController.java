package com.webdev.backend.controller;

import com.webdev.backend.model.Course;
import com.webdev.backend.model.Employee;
import com.webdev.backend.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/secure/course")
@SecurityRequirement(name = "bearerAuth")
public class CourseController {

	@Autowired
	private CourseService courseService;

	@PostMapping
	@Operation(summary = "创建课程", description = "提供课程名称、学分、开始时间和结束时间来创建新的课程。")
	public void createCourse(@RequestParam String courseName, @RequestParam Integer courseCredit,
			@RequestParam String startTime, @RequestParam String endTime) {
		Course course = new Course();
		course.setCourseName(courseName);
		course.setCourseCredit(courseCredit);
		course.setLastOperationTime();
		Date startTimeDate = new Date(Long.parseLong(startTime));
		Date endTimeDate = new Date(Long.parseLong(endTime));
		course.setStartTime(startTimeDate);
		course.setEndTime(endTimeDate);
		courseService.createCourse(course);
	}

	@PutMapping("/{id}")
	@Operation(summary = "更新课程", description = "根据提供的ID、课程名称、学分、开始时间和结束时间更新课程。")
	public void updateCourse(@PathVariable Long id, @RequestParam String courseName, @RequestParam Integer courseCredit,
			@RequestParam String startTime, @RequestParam String endTime) {
		Course course = new Course();
		course.setId(id);
		course.setCourseName(courseName);
		course.setCourseCredit(courseCredit);
		course.setLastOperationTime();
		course.setStartTime(new Date(Long.parseLong(startTime)));
		course.setEndTime(new Date(Long.parseLong(endTime)));
		courseService.updateCourse(course);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除课程", description = "根据提供的ID删除课程。")
	public void deleteCourse(@PathVariable Long id) {
		courseService.deleteCourse(id);
	}

	@PostMapping("/{courseId}/teacher/{teacherId}")
	@Operation(summary = "为课程添加教师", description = "根据提供的课程ID和教师ID为课程添加教师。")
	public void addTeacherToCourse(@PathVariable Long courseId, @PathVariable Long teacherId) {
		courseService.addTeacherToCourse(courseId, teacherId);
	}

	@PostMapping("/{courseId}/class/{classId}")
	@Operation(summary = "为课程添加班级", description = "根据提供的课程ID和班级ID为课程添加班级。")
	public void addClassToCourse(@PathVariable Long courseId, @PathVariable Long classId) {
		courseService.addClassToCourse(courseId, classId);
	}

	@DeleteMapping("/{courseId}/class/{classId}")
	@Operation(summary = "从课程中移除班级", description = "根据提供的课程ID和班级ID从课程中移除班级。")
	public void removeClassFromCourse(@PathVariable Long courseId, @PathVariable Long classId) {
		courseService.removeClassFromCourse(courseId, classId);
	}

	@DeleteMapping("/{courseId}/teacher/{teacherId}")
	@Operation(summary = "从课程中移除教师", description = "根据提供的课程ID和教师ID从课程中移除教师。")
	public void removeTeacherFromCourse(@PathVariable Long courseId, @PathVariable Long teacherId) {
		courseService.removeTeacherFromCourse(courseId, teacherId);
	}

	@GetMapping("/{courseId}/teacher")
	@Operation(summary = "获取课程的教师列表", description = "根据提供的课程ID获取课程的教师列表。")
	public List<Employee> getTeachers(@PathVariable Long courseId) {
		List<Employee> teachers = courseService.getTeachers(courseId);
		return teachers != null ? teachers : new ArrayList<>();
	}

	@GetMapping("/{courseId}/class")
	@Operation(summary = "获取课程的班级列表", description = "根据提供的课程ID获取课程的班级列表。")
	public List<com.webdev.backend.model.Class> getClasses(@PathVariable Long courseId) {
		List<com.webdev.backend.model.Class> classes = courseService.getClasses(courseId);
		return classes != null ? classes : new ArrayList<>();
	}

	@GetMapping("/search")
	@Operation(summary = "搜索课程", description = "根据课程名称、学分、结束时间的开始和结束范围、开始和结束的索引来搜索课程。")
	public List<Course> searchCourses(@RequestParam(required = false) String courseName,
			@RequestParam(required = false) Integer courseCredit, @RequestParam(required = false) String endTimeStart,
			@RequestParam(required = false) String endTimeEnd,
			@RequestParam(required = false, defaultValue = "0") Long start,
			@RequestParam(required = false, defaultValue = "128") Long end) {

		Date startTimeDate = null;
		Date endTimeDate = null;
		if (endTimeStart != null) {
			startTimeDate = new Date(Long.parseLong(endTimeStart));
		}
		if (endTimeEnd != null) {
			endTimeDate = new Date(Long.parseLong(endTimeEnd));
		}

		return courseService.searchCourses(courseName, courseCredit, startTimeDate, endTimeDate, start, end);
	}

}