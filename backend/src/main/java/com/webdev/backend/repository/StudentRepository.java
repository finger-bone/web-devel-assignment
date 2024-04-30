package com.webdev.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.webdev.backend.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

	Student findByPhoneNumber(String phoneNumber);

	Student findByStudentNumber(String studentNumber);

	Optional<Student> findById(Long id);

	Long countByGender(String gender);

	Long countByHighestEducation(String highestEducation);

}
