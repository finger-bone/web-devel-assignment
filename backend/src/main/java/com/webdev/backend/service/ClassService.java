package com.webdev.backend.service;

import com.webdev.backend.model.Class;
import com.webdev.backend.repository.ClassRepository;

import jakarta.persistence.criteria.Predicate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Class> searchClasses(String className, Timestamp endTimeStart, Timestamp endTimeEnd) {
        return classRepository.findAll((root, query, cb) -> {
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
        });
    }

    public Class getClassByClassName(String className) {
        // You need to implement this method in your ClassRepository using @Query annotation or using Spring Data JPA's method naming conventions
        return classRepository.findByClassName(className);
    }
}