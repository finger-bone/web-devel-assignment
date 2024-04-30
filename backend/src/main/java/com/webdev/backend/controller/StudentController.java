package com.webdev.backend.controller;

import com.webdev.backend.model.Student;
import com.webdev.backend.service.StudentService;

import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/secure/student")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

	public boolean validateStudentForm(Student student, String excludedNumber, String excludedPhone) {
		if (!StringUtils.hasText(student.getName()) || student.getName().length() < 2 || student.getName().length() > 10 || !student.getName().matches("[\\u4e00-\\u9fa5a-zA-Z0-9]+")) {
			return false;
		}

		if (!StringUtils.hasText(student.getStudentNumber()) || student.getStudentNumber().length() != 10 || !student.getStudentNumber().matches("[a-zA-Z0-9]+")) {
			return false;
		}

		if (StringUtils.hasText(excludedNumber) && !validateStudentNumber(student.getStudentNumber(), excludedNumber)) {
			return false;
		}

		if (!StringUtils.hasText(student.getGender()) || (!student.getGender().equals("男性") && !student.getGender().equals("女性"))) {
			return false;
		}

		if (!StringUtils.hasText(student.getPhoneNumber()) || student.getPhoneNumber().length() != 11 || !student.getPhoneNumber().matches("\\d{11}")) {
			return false;
		}

		if (StringUtils.hasText(excludedPhone) && !validatePhoneNumber(student.getPhoneNumber(), excludedPhone)) {
			return false;
		}

		if (StringUtils.hasText(student.getHighestEducation()) && !Arrays.asList("初中", "高中", "大专", "本科", "硕士", "博士").contains(student.getHighestEducation())) {
			return false;
		}

		if (student.getClassId() == null) {
			return false;
		}

		return true;
	}

	@Autowired
	private StudentService studentService;

	@GetMapping("/search")
	@Operation(summary = "搜索学生", description = "根据提供的参数搜索学生。")
	public List<Student> searchStudents(@RequestParam(required = false) String name,
			@RequestParam(required = false) String className, @RequestParam(required = false) String studentNumber,
			@RequestParam(required = false) String highestEducation,
			@RequestParam(required = false, defaultValue = "0") Long start,
			@RequestParam(required = false, defaultValue = "128") Long end) {
		return studentService.searchStudents(name, className, studentNumber, highestEducation, start, end);
	}

	@PostMapping
	@Operation(summary = "创建新学生", description = "提供学生信息来创建新学生。")
	public void createStudent(@RequestParam String name, @RequestParam String studentNumber,
			@RequestParam Integer classId, @RequestParam String gender, @RequestParam String phoneNumber,
			@RequestParam String highestEducation) {
		var student = new Student();
		student.setName(name);
		student.setStudentNumber(studentNumber);
		student.setClassId(classId);
		student.setGender(gender);
		student.setPhoneNumber(phoneNumber);
		student.setHighestEducation(highestEducation);
		student.setDisciplinaryActions(0);
		student.setDisciplinaryPoints(0);
		student.setLastOperationTime();
		if(!validateStudentForm(student, null, null)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "学生信息无效");
		}
		studentService.addStudent(student);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除学生", description = "根据提供的 ID 删除学生。")
	public void deleteStudent(@PathVariable Long id) {
		studentService.deleteStudent(id);
	}

	@PutMapping("/{id}")
	@Operation(summary = "更新学生信息", description = "根据提供的 ID 和学生信息更新学生。")
	public void updateStudent(@PathVariable Long id, @RequestParam String name, @RequestParam String studentNumber,
			@RequestParam Integer classId, @RequestParam String gender, @RequestParam String phoneNumber,
			@RequestParam String highestEducation) {
		var student = studentService.getStudentById(id);
		var originalPhoneNumber = student.getPhoneNumber();
		var originalStudentNumber = student.getStudentNumber();
		student.setName(name);
		student.setStudentNumber(studentNumber);
		student.setClassId(classId);
		student.setGender(gender);
		student.setPhoneNumber(phoneNumber);
		student.setHighestEducation(highestEducation);
		student.setDisciplinaryActions(student.getDisciplinaryActions());
		student.setDisciplinaryPoints(student.getDisciplinaryPoints());
		student.setLastOperationTime();
		if(!validateStudentForm(student, originalStudentNumber, originalPhoneNumber)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "学生信息无效");
		}
		studentService.updateStudent(student);
	}

	@PutMapping("/disciplinary/{id}")
	@Operation(summary = "更新学生违纪", description = "根据提供的 ID 和违纪信息更新学生。")
	public void updateDisciplinaryActions(@PathVariable Long id, @RequestParam Integer deltaDisciplinaryActions,
			@RequestParam Integer deltaDisciplinaryPoints) {
		var student = studentService.getStudentById(id);
		if (student != null) {
			var studentInstance = student;
			studentInstance.setDisciplinaryActions(studentInstance.getDisciplinaryActions() + deltaDisciplinaryActions);
			studentInstance.setDisciplinaryPoints(studentInstance.getDisciplinaryPoints() + deltaDisciplinaryPoints);
			studentInstance.setLastOperationTime();
			studentService.updateStudent(studentInstance);
		}
	}

	@GetMapping("/valid/number/{studentNumber}")
	@Operation(summary = "验证学号是否可用", description = "根据提供的学号验证其是否已经存在。")
	public boolean validateStudentNumber(@PathVariable String studentNumber,
			@RequestParam(required = false) String excluded) {
		var student = studentService.getStudentByStudentNumber(studentNumber);
		if (student == null) {
			return true;
		}
		else if (excluded != null && student.getStudentNumber().equals(excluded)) {
			return true;
		}
		else {
			return false;
		}
	}

	@GetMapping("/valid/phone/{phoneNumber}")
	@Operation(summary = "验证电话号码是否可用", description = "根据提供的电话号码验证其是否已经存在。")
	public boolean validatePhoneNumber(@PathVariable String phoneNumber,
			@RequestParam(required = false) String excluded) {
		var student = studentService.getStudentByPhoneNumber(phoneNumber);
		if (student == null) {
			return true;
		}
		else if (excluded != null && student.getPhoneNumber().equals(excluded)) {
			return true;
		}
		else {
			return false;
		}
	}

	@GetMapping("/statistics/gender")
	@Operation(summary = "性别统计", description = "获取学生性别统计信息。")
	public java.util.Map<String, Integer> genderStatistics() {
		return studentService.getStudentCountByGender();
	}

	@GetMapping("/statistics/education")
	@Operation(summary = "学历统计", description = "获取学生学历统计信息。")
	public java.util.Map<String, Integer> educationStatistics() {
		return studentService.getStudentCountByEducation();
	}

}