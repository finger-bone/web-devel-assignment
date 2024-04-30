package com.webdev.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "Courses")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "course_name")
	private String courseName;

	@Column(name = "course_credit")
	private Integer courseCredit;

	@Column(name = "last_operation_time")
	private Timestamp lastOperationTime;

	@Column(name = "start_time")
	private Date startTime;

	@Column(name = "end_time")
	private Date endTime;

	public void setLastOperationTime() {
		this.lastOperationTime = new Timestamp(System.currentTimeMillis());
	}

}