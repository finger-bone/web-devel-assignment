package com.webdev.backend.repository;

import com.webdev.backend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Employee findByUsername(String username);
    long countByGender(String gender);
    long countByPosition(String position);
}