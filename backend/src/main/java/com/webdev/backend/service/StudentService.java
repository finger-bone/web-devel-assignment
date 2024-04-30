package com.webdev.backend.service;

import com.webdev.backend.model.Student;
import com.webdev.backend.repository.ClassRepository;
import com.webdev.backend.repository.StudentRepository;

import jakarta.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private ClassRepository classRepository;

	public List<Student> searchStudents(String name, String className, String studentNumber, String highestEducation,
			Long start, Long end) {
		List<Student> students = studentRepository.findAll((root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (name != null) {
				predicates.add(cb.like(root.get("name"), "%" + name + "%"));
			}
			if (studentNumber != null) {
				predicates.add(cb.like(root.get("studentNumber"), "%" + studentNumber + "%"));
			}
			if (highestEducation != null) {
				predicates.add(cb.like(root.get("highestEducation"), "%" + highestEducation + "%"));
			}
			if (className != null) {
				List<Long> classIds = classRepository.findByClassNameLike("%" + className + "%")
					.stream()
					.map(c -> c.getId())
					.collect(Collectors.toList());
				if (!classIds.isEmpty()) {
					predicates.add(root.get("classId").in(classIds));
				}
				else {
					// If no classes are found, return an empty list
					query.where(cb.literal(false));
				}
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		});
		return students.subList(Math.min(start.intValue(), students.size()), Math.min(end.intValue(), students.size()));
	}

	public Student getStudentByStudentNumber(String studentNumber) {
		return studentRepository.findByStudentNumber(studentNumber);
	}

	public void addStudent(Student student) {
		studentRepository.save(student);
	}

	public void deleteStudent(Long id) {
		studentRepository.deleteById(id);
	}

	public Student getStudentById(Long id) {
		return studentRepository.findById(id).orElse(null);
	}

	public void updateStudent(Student student) {
		studentRepository.save(student);
	}

	public Student getStudentByPhoneNumber(String phoneNumber) {
		return studentRepository.findByPhoneNumber(phoneNumber);
	}

	public Map<String, Integer> getStudentCountByGender() {
		String[] genders = { "男性", "女性" };
		Map<String, Integer> result = new HashMap<>();
		for (String gender : genders) {
			long count = studentRepository.countByGender(gender);
			if (count > 0) {
				result.put(gender, (int) count);
			}
		}
		return result;
	}

	public Map<String, Integer> getStudentCountByEducation() {
		// 初中 高中 大专 本科 硕士 博士
		String[] educations = { "初中", "高中", "大专", "本科", "硕士", "博士" };
		Map<String, Integer> result = new HashMap<>();
		for (String education : educations) {
			long count = studentRepository.countByHighestEducation(education);
			if (count > 0) {
				result.put(education, (int) count);
			}
		}
		return result;
	}

}
