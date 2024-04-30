package com.webdev.backend.repository;

import com.webdev.backend.model.Class;
import com.webdev.backend.model.Student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public interface ClassRepository extends JpaRepository<Class, Long>, JpaSpecificationExecutor<Class> {

	Class findByClassName(String className);

	List<Student> findByClassNameLike(String string);

}