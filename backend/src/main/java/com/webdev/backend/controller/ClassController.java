package com.webdev.backend.controller;

import com.webdev.backend.model.Class;
import com.webdev.backend.service.ClassService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/secure/class")
public class ClassController {
    @Autowired
    private ClassService classService;

    @PostMapping
    public Class createClass(
        @RequestParam String className,
        @RequestParam String classroom,
        @RequestParam(value = "startTime") String startTimeStr,
        @RequestParam(value = "endTime") String endTimeStr,
        @RequestParam(value = "headTeacherId") int headTeacherId) {
        Timestamp startTime = new Timestamp(Long.parseLong(startTimeStr));
        Timestamp endTime = new Timestamp(Long.parseLong(endTimeStr));
        Class classUnit = new Class();
        classUnit.setStartTime(startTime);
        classUnit.setEndTime(endTime);
        classUnit.setClassName(className);
        classUnit.setClassroom(classroom);
        classUnit.setHeadTeacherId(headTeacherId);
        return classService.createClass(classUnit);
    }

    @PutMapping("/{id}")
    public void updateClass(
        @PathVariable Long id,
        @RequestParam String className,
        @RequestParam String classroom,
        @RequestParam(value = "startTime") String startTimeStr,
        @RequestParam(value = "endTime") String endTimeStr,
        @RequestParam(value = "headTeacherId") int headTeacherId) {
        var classUnit = new Class();
        classUnit.setId(id);
        classUnit.setClassName(className);
        classUnit.setClassroom(classroom);
        classUnit.setStartTime(new Timestamp(Long.parseLong(startTimeStr)));
        classUnit.setEndTime(new Timestamp(Long.parseLong(endTimeStr)));
        classUnit.setHeadTeacherId(headTeacherId);
        classService.updateClass(classUnit);
    }

    @DeleteMapping("/{id}")
    public void deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
    }

    @GetMapping("/search")
    public List<Class> getClasses(
        @RequestParam(required = false) String className,
        @RequestParam(value = "endTimeStart", required = false) String endTimeStartStr,
        @RequestParam(value = "endTimeEnd", required = false) String endTimeEndStr) {
        Timestamp endTimeStart = null;
        Timestamp endTimeEnd = null;
        if (endTimeStartStr != null) {
            endTimeStart = new Timestamp(Long.parseLong(endTimeStartStr));
        }
        if (endTimeEndStr != null) {
            endTimeEnd = new Timestamp(Long.parseLong(endTimeEndStr));
        }
        return classService.searchClasses(className, endTimeStart, endTimeEnd);
    }

    @GetMapping("/validate/{name}")
    public boolean validName(@PathVariable String name, @RequestParam(required = false) String excluded){
        return name.equals(excluded) || classService.getClassByClassName(name) == null;
    }
}