package com.webdev.backend.service;

import com.webdev.backend.model.Employee;
import com.webdev.backend.repository.EmployeeRepository;

import jakarta.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	public Employee createEmployee(Employee employee) {
		return employeeRepository.save(employee);
	}

	public void updateEmployee(Employee employee) {
		employeeRepository.save(employee);
	}

	public void deleteEmployee(Long id) {
		employeeRepository.deleteById(id);
	}

	public List<Employee> searchEmployees(String name, String userName, String gender, Date hireDateStart,
			Date hireDateEnd, String position, Long start, Long end) {
		List<Employee> employees = employeeRepository.findAll((root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (name != null) {
				predicates.add(cb.like(root.get("name"), "%" + name + "%"));
			}
			if (userName != null) {
				predicates.add(cb.like(root.get("username"), "%" + userName + "%"));
			}
			if (gender != null) {
				predicates.add(cb.like(root.get("gender"), "%" + gender + "%"));
			}
			if (hireDateStart != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("hireDate"), hireDateStart));
			}
			if (hireDateEnd != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("hireDate"), hireDateEnd));
			}
			if (position != null) {
				predicates.add(cb.like(root.get("position"), "%" + position + "%"));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		});
		return employees.subList(Math.min(start.intValue(), employees.size()),
				Math.min(end.intValue(), employees.size()));
	}

	public Employee getEmployeeByUsername(String username) {
		return employeeRepository.findByUsername(username);
	}

	public Map<String, Integer> getEmployeeCountByGender() {
		String[] genders = { "男性", "女性" };
		Map<String, Integer> result = new HashMap<>();
		for (String gender : genders) {
			long count = employeeRepository.countByGender(gender);
			if (count != 0) {
				result.put(gender, (int) count);
			}
		}
		return result;
	}

	public Map<String, Integer> getEmployeeCountByPosition() {
		String[] positions = { "班主任", "讲师", "学工主管", "教研主管" };
		Map<String, Integer> result = new HashMap<>();
		for (String position : positions) {
			long count = employeeRepository.countByPosition(position);
			if (count != 0) {
				result.put(position, (int) count);
			}
		}
		return result;
	}

	public Employee getEmployeeById(Long id) {
		return employeeRepository.findById(id).orElse(null);
	}

}