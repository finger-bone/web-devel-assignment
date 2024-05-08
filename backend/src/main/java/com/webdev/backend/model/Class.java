package com.webdev.backend.model;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "classes")
@Entity
public class Class {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String className;

	private String classroom;

	private Date startTime;

	private Date endTime;

	private Integer headTeacherId;

	private Timestamp lastOperationTime;

	public void setEndTime(Timestamp endTime) {
		this.endTime = new Date(endTime.getTime());
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = new Date(startTime.getTime());
	}

	public void setLastOperationTime() {
		this.lastOperationTime = new Timestamp(System.currentTimeMillis());
	}
}