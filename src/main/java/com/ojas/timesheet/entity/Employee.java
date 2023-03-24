package com.ojas.timesheet.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EMPLOYEE")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SNID")
	private long id;
	
	@Column(name = "EMP_ID")
	private String empId;
	
	@Column(name = "EMP_NAME")
	private String empName;
	
	@Column(name = "PROJECT")
	private String project;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", empId=" + empId + ", empName=" + empName + ", project=" + project + "]";
	}

	public Employee(long id, String empId, String empName, String project) {
		super();
		this.id = id;
		this.empId = empId;
		this.empName = empName;
		this.project = project;
	}

	public Employee() {
		super();
	}
	
	
}
