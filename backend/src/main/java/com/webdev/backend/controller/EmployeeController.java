package com.webdev.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.webdev.backend.model.Employee;
import com.webdev.backend.model.EmployeeInfoResponse;
import com.webdev.backend.model.User;
import com.webdev.backend.service.EmployeeService;
import com.webdev.backend.service.UserService;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/secure/employee")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

	private boolean validateEmployeeForm(Employee employee, String excluded) {
		if (employee.getUsername() == null || employee.getUsername().length() < 2 || employee.getUsername().length() > 20
				|| !employee.getUsername().matches("^[a-zA-Z0-9]+$")) {
			return false;
		}

		if(
			employee.getUsername().equals("admin")
		) {
			return false;
		}

		if (!(
			(excluded != null && employee.getUsername().equals(excluded)) ||
			!userService.userExists(employee.getUsername()
		))) {
			return false;
		}

		if (employee.getName() == null || employee.getName().length() < 2 || employee.getName().length() > 10
				|| !employee.getName().matches("[\\u4e00-\\u9fa5]+")) {
			return false;
		}

		if (employee.getGender() == null || (!employee.getGender().equals("男性") && !employee.getGender().equals("女性"))) {
			return false;
		}

		if (employee.getImage() != null && employee.getImage().length > 2 * 1024 * 1024) {
			return false;
		}

		return true;
	}

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private UserService userService;

	@GetMapping("/info")
	@Operation(summary = "获取员工信息", description = "根据提供的用户名和角色获取员工信息。")
	public EmployeeInfoResponse getEmployeeInfo(@RequestAttribute("username") String username,
			@RequestAttribute("role") String role) {
		Employee employee = employeeService.getEmployeeByUsername(username);
		EmployeeInfoResponse employeeInfoResponse = new EmployeeInfoResponse();
		if (employee != null) {
			employeeInfoResponse.setUsername(employee.getUsername());
			employeeInfoResponse.setAvatar(employee.getImage());
			employeeInfoResponse.setId(employee.getId());
			employeeInfoResponse.setRole(role);
		}
		return employeeInfoResponse;
	}

	@PostMapping
	@Operation(summary = "创建新员工", description = "提供员工信息来创建新员工。")
	public Employee createEmployee(@RequestParam("name") String name, @RequestParam("userName") String userName,
			@RequestParam("gender") String gender, @RequestParam(value = "image", required = false) MultipartFile image,
			@RequestParam(value = "position", required = false) String position,
			@RequestParam(value = "hireDate", required = false) String hireDateStr,
			@RequestParam(value = "departmentId", required = false) Long departmentId) {

		var employee = new Employee();
		employee.setName(name);
		employee.setUsername(userName);
		employee.setGender(gender);
		try {
			if (image != null) {
				employee.setImage(image.getBytes());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		employee.setPosition(position);
		if (hireDateStr != null) {
			Timestamp hireDate = new Timestamp(Long.parseLong(hireDateStr));
			employee.setHireDate(hireDate);
		}
		employee.setLastOperationTime();
		employee.setDepartmentId(departmentId);
		if(!validateEmployeeForm(employee, null)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "员工信息无效");
		}
		userService.registerUser(User.builder().username(userName).password("123456").role("admin").build());
		return employeeService.createEmployee(employee);
	}

	@PutMapping("/{id}")
	@Operation(summary = "更新员工信息", description = "根据提供的ID和员工信息更新员工。")
	public void updateEmployee(@PathVariable Long id, @RequestParam("name") String name,
			@RequestParam("userName") String userName, @RequestParam("gender") String gender,
			@RequestParam(value = "image", required = false) MultipartFile image,
			@RequestParam(value = "position", required = false) String position,
			@RequestParam(value = "hireDate", required = false) String hireDateStr,
			@RequestParam(value = "departmentId", required = false) Long departmentId) {
		var employee = new Employee();
		employee.setId(id);
		employee.setName(name);
		employee.setUsername(userName);
		employee.setGender(gender);
		try {
			if (image != null) {
				employee.setImage(image.getBytes());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		employee.setPosition(position);
		if (hireDateStr != null) {
			Timestamp hireDate = new Timestamp(Long.parseLong(hireDateStr));
			employee.setHireDate(hireDate);
		}
		employee.setLastOperationTime();
		employee.setDepartmentId(departmentId);
		if(!validateEmployeeForm(employee, userName)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "员工信息无效");
		}
		var originalUsername = employeeService.getEmployeeById(id).getUsername();
		employeeService.updateEmployee(employee);
		if(originalUsername != null && originalUsername.equals(employee.getUsername())) {
			return;
		}
		userService.registerUser(User.builder().username(employee.getUsername()).password("123456").role("admin").build());
		userService.deleteUser(originalUsername);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除员工", description = "根据提供的ID删除员工。")
	public void deleteEmployee(@PathVariable Long id) {
		userService.deleteUser(employeeService.getEmployeeById(id).getUsername());
		employeeService.deleteEmployee(id);
	}

	@GetMapping("/search")
	@Operation(summary = "搜索员工", description = "根据提供的参数搜索员工。")
	public List<Employee> searchEmployees(@RequestParam(required = false) String name,
			@RequestParam(required = false) String userName, @RequestParam(required = false) String gender,
			@RequestParam(required = false) String hireDateStart, @RequestParam(required = false) String hireDateEnd,
			@RequestParam(required = false) String position,
			@RequestParam(required = false, defaultValue = "0") Long start,
			@RequestParam(required = false, defaultValue = "128") Long end) {
		Timestamp hireDateStartTimestamp = null;
		Timestamp hireDateEndTimestamp = null;
		if (hireDateStart != null) {
			hireDateStartTimestamp = new Timestamp(Long.parseLong(hireDateStart));
		}
		if (hireDateEnd != null) {
			hireDateEndTimestamp = new Timestamp(Long.parseLong(hireDateEnd));
		}
		return employeeService.searchEmployees(name, userName, gender, hireDateStartTimestamp, hireDateEndTimestamp,
				position, start, end);
	}

	@GetMapping("/statistics/gender")
	@Operation(summary = "性别统计", description = "获取员工性别统计信息。")
	public Map<String, Integer> genderStatistics() {
		return employeeService.getEmployeeCountByGender();
	}

	@GetMapping("/statistics/position")
	@Operation(summary = "职位统计", description = "获取员工职位统计信息。")
	public Map<String, Integer> getMethodName() {
		return employeeService.getEmployeeCountByPosition();
	}

}