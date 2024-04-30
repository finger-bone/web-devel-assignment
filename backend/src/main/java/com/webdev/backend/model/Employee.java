package com.webdev.backend.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "employees")
@Entity
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String username;

	private byte[] image;

	private String gender;

	private String position;

	private Date hireDate;

	private Timestamp lastOperationTime;

	private Long departmentId;

	public Timestamp getLastOperationTime() {
		return lastOperationTime;
	}

	public void setLastOperationTime() {
		this.lastOperationTime = new Timestamp(System.currentTimeMillis());
	}

	public void setHireDate(java.util.Date hireDate) {
		if (hireDate == null) {
			this.hireDate = null;
			return;
		}
		this.hireDate = new Date(hireDate.getTime());
	}

}