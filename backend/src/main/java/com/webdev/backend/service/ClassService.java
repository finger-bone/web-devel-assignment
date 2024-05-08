package com.webdev.backend.service;

import com.webdev.backend.model.Class;
import com.webdev.backend.repository.ClassRepository;

import jakarta.persistence.criteria.Predicate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ClassService {

	@Autowired
	private ClassRepository classRepository;

	public Class createClass(Class classUnit) {
		return classRepository.save(classUnit);
	}

	public Class updateClass(Class classUnit) {
		return classRepository.save(classUnit);
	}

	public void deleteClass(Long id) {
		classRepository.deleteById(id);
	}

	public List<Class> searchClasses(String className, Date endTimeStart, Date endTimeEnd, Long start, Long end) {
		List<Class> classes = classRepository.findAll((root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (className != null) {
				predicates.add(cb.like(root.get("className"), "%" + className + "%"));
			}
			if (endTimeStart != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("endTime"), endTimeStart));
			}
			if (endTimeEnd != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("endTime"), endTimeEnd));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		}, Sort.by(Sort.Direction.DESC, "lastOperationTime"));
		return classes.subList(Math.min(start.intValue(), classes.size()), Math.min(end.intValue(), classes.size()));
	}

	public Class getClassByClassName(String className) {
		return classRepository.findByClassName(className);
	}

	public Class getClassById(Long id) {
		return classRepository.findById(id).orElse(null);
	}
}