package com.webdev.backend.controller;

import com.webdev.backend.model.Department;
import com.webdev.backend.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/secure/department")
@SecurityRequirement(name = "bearerAuth")
public class DepartmentController {

	@Autowired
	private DepartmentService departmentService;

	@PostMapping
	@Operation(summary = "创建部门", description = "提供部门名称来创建新的部门。")
	public Department createDepartment(@RequestParam String departmentName) {
		var department = new Department();
		if(!validateDepartmentName(departmentName, null)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "部门名称已存在");
		}
		if(departmentName.length() < 2 || departmentName.length() > 10) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "部门名称长度应在2-20之间");
		}
		department.setDepartmentName(departmentName);
		department.setLastOperationTime();
		return departmentService.createDepartment(department);
	}

	@PutMapping("/{id}")
	@Operation(summary = "更新部门", description = "根据提供的ID和部门名称更新部门。")
	public void updateDepartment(@PathVariable Long id, @RequestParam String departmentName) {
		var department = new Department();
		if(!validateDepartmentName(departmentName, department.getDepartmentName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "部门名称已存在");
		}
		if(departmentName.length() < 2 || departmentName.length() > 10) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "部门名称长度应在2-20之间");
		}
		department.setId(id);
		department.setDepartmentName(departmentName);
		department.setLastOperationTime();
		System.out.println(department);
		departmentService.updateDepartment(department);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除部门", description = "根据提供的ID删除部门。")
	public void deleteDepartment(@PathVariable Long id) {
		departmentService.deleteDepartment(id);
	}

	@GetMapping
	@Operation(summary = "获取所有部门", description = "返回系统中所有的部门。")
	public List<Department> getDepartments() {
		return departmentService.getDepartments();
	}

	@GetMapping("/valid/{departmentName}")
	@Operation(summary = "检查部门名称是否可用", description = "提供部门名称来检查其有效性。")
	public boolean validateDepartmentName(@PathVariable String departmentName, @RequestParam(required = false) String excluded) {
		if (departmentName.equals(excluded)) {
			return true;
		} else {
			return departmentService.getDepartmentByDepartmentName(departmentName) == null;
		}
	}
	
}