package com.webdev.backend.model;

import java.sql.Timestamp;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "students")
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String studentNumber;

	private Integer classId;

	private String gender;

	private String phoneNumber;

	private String highestEducation;

	private Integer disciplinaryActions;

	private Integer disciplinaryPoints;

	private Timestamp lastOperationTime;

	@ManyToOne
	@JoinColumn(name = "classId", insertable = false, updatable = false)
	private Class classUnit;

	public void setLastOperationTime() {
		this.lastOperationTime = new Timestamp(System.currentTimeMillis());
	}

}