package com.webdev.backend.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.webdev.backend.model.Employee;
import com.webdev.backend.model.EmployeeInfoResponse;
import com.webdev.backend.service.EmployeeService;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/secure/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/info")
    public EmployeeInfoResponse getEmployeeInfo(HttpServletRequest request) {
        String username = request.getAttribute("username").toString();
        Employee employee = employeeService.getEmployeeByUsername(username);
        EmployeeInfoResponse employeeInfoResponse = new EmployeeInfoResponse();
        if(employee != null) {
            employeeInfoResponse.setUsername(employee.getUsername());
            employeeInfoResponse.setAvatar(employee.getImage());
            employeeInfoResponse.setId(employee.getId());
            employeeInfoResponse.setRole(request.getAttribute("role").toString());
        }
        return employeeInfoResponse;
    }
    

    @PostMapping
    public Employee createEmployee(
        @RequestParam("name") String name,
        @RequestParam("userName") String userName,
        @RequestParam("gender") String gender,
        @RequestParam(value = "image", required = false) MultipartFile image,
        @RequestParam(value = "position", required = false) String position,
        @RequestParam(value = "hireDate", required = false) String hireDateStr,
        @RequestParam(value = "departmentId", required = false) Long departmentId) {
        
        var employee = new Employee();
        employee.setName(name);
        employee.setUsername(userName);
        employee.setGender(gender);
        try {
            if(image != null) {
                employee.setImage(image.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        employee.setPosition(position);
        if (hireDateStr != null) {
            Timestamp hireDate = new Timestamp(Long.parseLong(hireDateStr));
            employee.setHireDate(hireDate);
        }
        employee.setLastOperationTime();
        employee.setDepartmentId(departmentId);
        return employeeService.createEmployee(employee);
    }

    @PutMapping("/{id}")
    public void updateEmployee(@PathVariable Long id,
    @RequestParam("name") String name,
        @RequestParam("userName") String userName,
        @RequestParam("gender") String gender,
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
            if(image != null) {
                employee.setImage(image.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        employee.setPosition(position);
        if (hireDateStr != null) {
            Timestamp hireDate = new Timestamp(Long.parseLong(hireDateStr));
            employee.setHireDate(hireDate);
        }
        employee.setLastOperationTime();
        employee.setDepartmentId(departmentId);
        employeeService.updateEmployee(employee);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }

    @GetMapping("/search")
    public List<Employee> searchEmployees(@RequestParam(required = false) String name, @RequestParam(required = false) String userName, @RequestParam(required = false) String gender, @RequestParam(required = false) String hireDateStart, @RequestParam(required = false) String hireDateEnd, @RequestParam(required = false) String position) {
        Timestamp hireDateStartTimestamp = null;
        Timestamp hireDateEndTimestamp = null;
        if (hireDateStart != null) {
            hireDateStartTimestamp = new Timestamp(Long.parseLong(hireDateStart));
        }
        if (hireDateEnd != null) {
            hireDateEndTimestamp = new Timestamp(Long.parseLong(hireDateEnd));
        }
        return employeeService.searchEmployees(name, userName, gender, hireDateStartTimestamp, hireDateEndTimestamp, position);
    }

    @GetMapping("/validate/{name}")
    public boolean validName(@PathVariable String name, @RequestParam(required = false) String excluded) {
        return name.equals(excluded) || employeeService.getEmployeeByUsername(name) == null;
    }
    
    @GetMapping("/statistics/gender")
    public Map<String, Integer> genderStatistics() {
        return employeeService.getEmployeeCountByGender();
    }

    @GetMapping("/statistics/position")
    public Map<String, Integer> getMethodName() {
        return employeeService.getEmployeeCountByPosition();
    }
}