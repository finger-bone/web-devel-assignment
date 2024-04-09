package com.webdev.backend.controller;

import com.webdev.backend.model.Student;
import com.webdev.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/secure/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/search")
    public List<Student> searchStudents(@RequestParam(required = false) String name, 
                                        @RequestParam(required = false) String className, 
                                        @RequestParam(required = false) String studentNumber, 
                                        @RequestParam(required = false) String highestEducation) {
        return studentService.searchStudents(name, className, studentNumber, highestEducation);
    }

    @PostMapping
    public void addStudent(
        @RequestParam String name,
        @RequestParam String studentNumber,
        @RequestParam Integer classId,
        @RequestParam String gender,
        @RequestParam String phoneNumber,
        @RequestParam String highestEducation
    ) {
        var student = new Student();
        student.setName(name);
        student.setStudentNumber(studentNumber);
        student.setClassId(classId);
        student.setGender(gender);
        student.setPhoneNumber(phoneNumber);
        student.setHighestEducation(highestEducation);
        student.setDisciplinaryActions(0);
        student.setDisciplinaryPoints(0);
        student.setLastOperationTime();
        studentService.addStudent(student);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @PutMapping("/{id}")
    public void updateStudent(
        @PathVariable Long id,
        @RequestParam String name,
        @RequestParam String studentNumber,
        @RequestParam Integer classId,
        @RequestParam String gender,
        @RequestParam String phoneNumber,
        @RequestParam String highestEducation
    ) {
        var student = studentService.getStudentById(id);
        student.setName(name);
        student.setStudentNumber(studentNumber);
        student.setClassId(classId);
        student.setGender(gender);
        student.setPhoneNumber(phoneNumber);
        student.setHighestEducation(highestEducation);
        student.setDisciplinaryActions(student.getDisciplinaryActions());
        student.setDisciplinaryPoints(student.getDisciplinaryPoints());
        student.setLastOperationTime();
        studentService.updateStudent(student);
    }

    @PutMapping("/disciplinary/{id}")
    public void updateDisciplinaryActions(@PathVariable Long id, 
                                          @RequestParam Integer deltaDisciplinaryActions, 
                                          @RequestParam Integer deltaDisciplinaryPoints) {
        var student = studentService.getStudentById(id);
        if (student != null) {
            var studentInstance = student;
            studentInstance.setDisciplinaryActions(studentInstance.getDisciplinaryActions() + deltaDisciplinaryActions);
            studentInstance.setDisciplinaryPoints(studentInstance.getDisciplinaryPoints() + deltaDisciplinaryPoints);
            studentInstance.setLastOperationTime();
            studentService.updateStudent(studentInstance);
        }
    }

    @GetMapping("/valid/number/{studentNumber}")
    public boolean validateStudentNumber(@PathVariable String studentNumber, @RequestParam(required = false) String excluded) {
        var student = studentService.getStudentByStudentNumber(studentNumber);
        if (student == null) {
            return true;
        } else if(excluded != null && student.getStudentNumber().equals(excluded)) {
            return true;
        } else {
            return false;
        }
    }

    @GetMapping("/valid/phone/{phoneNumber}")
    public boolean validatePhoneNumber(@PathVariable String phoneNumber, @RequestParam(required = false) String excluded) {
        var student = studentService.getStudentByPhoneNumber(phoneNumber);
        if (student == null) {
            return true;
        } else if(excluded != null && student.getPhoneNumber().equals(excluded)) {
            return true;
        } else {
            return false;
        }
    }

    @GetMapping("/statistics/gender")
    public java.util.Map<String, Integer> genderStatistics() {
        return studentService.getStudentCountByGender();
    }

    @GetMapping("/statistics/education")
    public java.util.Map<String, Integer> educationStatistics() {
        return studentService.getStudentCountByEducation();
    }
}