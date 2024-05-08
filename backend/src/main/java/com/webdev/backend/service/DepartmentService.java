package com.webdev.backend.service;

import com.webdev.backend.model.Department;
import com.webdev.backend.repository.DepartmentRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

	@Autowired
	private DepartmentRepository departmentRepository;

	public Department createDepartment(Department department) {
		return departmentRepository.save(department);
	}

	public Department updateDepartment(Department department) {
		return departmentRepository.save(department);
	}

	public void deleteDepartment(Long id) {
		departmentRepository.deleteById(id);
	}

	public List<Department> getDepartments() {
		return departmentRepository.findAll(Sort.by(Sort.Direction.DESC, "lastOperationTime"));
	}

	public Department getDepartmentByDepartmentName(String departmentName) {
		return departmentRepository.findByDepartmentName(departmentName);
	}
}