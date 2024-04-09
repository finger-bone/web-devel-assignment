package com.webdev.backend.controller;

import com.webdev.backend.model.Department;
import com.webdev.backend.model.DepartmentRequest;
import com.webdev.backend.service.DepartmentService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/secure/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public Department createDepartment(@RequestBody DepartmentRequest departmentRequest) {
        var department = new Department();
        department.setDepartmentName(departmentRequest.getDepartmentName());
        department.setLastOperationTime();
        return departmentService.createDepartment(department);
    }

    @PutMapping("/{id}")
    public void updateDepartment(@PathVariable Long id, @RequestBody DepartmentRequest departmentRequest) {
        var department = new Department();
        department.setId(id);
        department.setDepartmentName(departmentRequest.getDepartmentName());
        department.setLastOperationTime();
        System.out.println(department);
        departmentService.updateDepartment(department);
    }

    @DeleteMapping("/{id}")
    public void deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
    }

    @GetMapping
    public List<Department> getDepartments() {
        return departmentService.getDepartments();
    }
    
}