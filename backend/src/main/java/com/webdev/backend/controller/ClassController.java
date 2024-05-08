package com.webdev.backend.controller;

import com.webdev.backend.model.Class;
import com.webdev.backend.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/secure/class")
@SecurityRequirement(name = "bearerAuth")
public class ClassController {

	@Autowired
	private ClassService classService;

	public boolean validateClassForm(Class classUnit, String excluded) {
		if(classUnit.getClassName() == null || classUnit.getClassName().length() < 4 || classUnit.getClassName().length() > 30 || !classUnit.getClassName().matches("[\\u4e00-\\u9fa5a-zA-Z0-9]+")) {
			return false;
		}
		if(!validName(classUnit.getClassName(), excluded)) {
			return false;
		}
		if(classUnit.getClassroom() != null && (classUnit.getClassroom().length() > 20 || !classUnit.getClassroom().matches("[\\u4e00-\\u9fa5a-zA-Z0-9]*"))) {
			return false;
		}
		if(classUnit.getStartTime() == null) {
			return false;
		}
		if(classUnit.getEndTime() == null) {
			return false;
		}
		if(classUnit.getStartTime().getTime() >= classUnit.getEndTime().getTime()) {
			return false;
		}
		return true;
	}

	@PostMapping
	@Operation(summary = "创建班级", description = "提供班级名称、教室、开始时间、结束时间和班主任ID来创建新的班级。")
	public Class createClass(@RequestParam String className, @RequestParam(required = false, defaultValue = "") String classroom,
			@RequestParam(value = "startTime") String startTimeStr, @RequestParam(value = "endTime") String endTimeStr,
			@RequestParam(value = "headTeacherId") int headTeacherId) {
		Timestamp startTime = new Timestamp(Long.parseLong(startTimeStr));
		Timestamp endTime = new Timestamp(Long.parseLong(endTimeStr));
		Class classUnit = new Class();
		classUnit.setStartTime(startTime);
		classUnit.setEndTime(endTime);
		classUnit.setClassName(className);
		classUnit.setClassroom(classroom);
		classUnit.setHeadTeacherId(headTeacherId);
		classUnit.setLastOperationTime();
		if(!validateClassForm(classUnit, null)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "班级信息无效");
		}
		return classService.createClass(classUnit);
	}

	@PutMapping("/{id}")
	@Operation(summary = "更新班级", description = "根据提供的ID、班级名称、教室、开始时间、结束时间和班主任ID更新班级。")
	public void updateClass(@PathVariable Long id, @RequestParam String className, @RequestParam(required = false, defaultValue = "") String classroom,
			@RequestParam(value = "startTime") String startTimeStr, @RequestParam(value = "endTime") String endTimeStr,
			@RequestParam(value = "headTeacherId") int headTeacherId) {
		var classUnit = new Class();
		var originalName = classService.getClassById(id).getClassName();
		classUnit.setId(id);
		classUnit.setClassName(className);
		classUnit.setClassroom(classroom);
		classUnit.setStartTime(new Timestamp(Long.parseLong(startTimeStr)));
		classUnit.setEndTime(new Timestamp(Long.parseLong(endTimeStr)));
		classUnit.setHeadTeacherId(headTeacherId);
		classUnit.setLastOperationTime();
		if(!validateClassForm(classUnit, originalName)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "班级信息无效");
		}
		classService.updateClass(classUnit);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "删除班级", description = "根据提供的ID删除班级。")
	public void deleteClass(@PathVariable Long id) {
		classService.deleteClass(id);
	}

	@GetMapping("/search")
	@Operation(summary = "搜索班级", description = "根据班级名称、结束时间的开始和结束范围、开始和结束的索引来搜索班级。")
	public List<Class> getClasses(@RequestParam(required = false) String className,
			@RequestParam(value = "endTimeStart", required = false) String endTimeStartStr,
			@RequestParam(value = "endTimeEnd", required = false) String endTimeEndStr,
			@RequestParam(required = false, defaultValue = "0") Long start,
			@RequestParam(required = false, defaultValue = "128") Long end) {
		Timestamp endTimeStart = null;
		Timestamp endTimeEnd = null;
		if (endTimeStartStr != null) {
			endTimeStart = new Timestamp(Long.parseLong(endTimeStartStr));
		}
		if (endTimeEndStr != null) {
			endTimeEnd = new Timestamp(Long.parseLong(endTimeEndStr));
		}
		return classService.searchClasses(className, endTimeStart, endTimeEnd, start, end);
	}

	@GetMapping("/valid/{name}")
	@Operation(summary = "验证班级名称", description = "验证班级名称是否有效。如果名称等于排除的名称或者班级名称不存在，则返回true。")
	public boolean validName(@PathVariable String name, @RequestParam(required = false) String excluded) {
		return name.equals(excluded) || classService.getClassByClassName(name) == null;
	}

}