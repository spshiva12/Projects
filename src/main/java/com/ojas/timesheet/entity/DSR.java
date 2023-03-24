package com.ojas.timesheet.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "DSR")
public class DSR {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SNID")
	private long id;
	
	@Column(name = "EMP_NAME")
	private String empName;
	
	@Column(name = "PROJECT")
	private String project;
	
	@Column(name = "DSR_DATE")
	private Date dSRDate;
	
	@Column(name = "HRS_WORKED")
	private String hrsWorked;
	
	@Column(name = "DSR_REPORT",columnDefinition = "MEDIUMTEXT")
	private String dSRReport;
	
	@Column(name = "SUBMIT_DATE")
	private Date submitDate;
	
	@Column(name = "VISIBILITY")
	private boolean visibility;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public Date getdSRDate() {
		return dSRDate;
	}

	public void setdSRDate(Date dSRDate) {
		this.dSRDate = dSRDate;
	}

	public String getHrsWorked() {
		return hrsWorked;
	}

	public void setHrsWorked(String hrsWorked) {
		this.hrsWorked = hrsWorked;
	}

	public String getdSRReport() {
		return dSRReport;
	}

	public void setdSRReport(String dSRReport) {
		this.dSRReport = dSRReport;
	}

	public Date getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}

	public boolean isVisibility() {
		return visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	@Override
	public String toString() {
		return "DSR [id=" + id + ", empName=" + empName + ", project=" + project + ", dSRDate=" + dSRDate
				+ ", hrsWorked=" + hrsWorked + ", dSRReport=" + dSRReport + ", submitDate=" + submitDate
				+ ", visibility=" + visibility + "]";
	}

	public DSR(long id, String empName, String project, Date dSRDate, String hrsWorked, String dSRReport,
			Date submitDate, boolean visibility) {
		super();
		this.id = id;
		this.empName = empName;
		this.project = project;
		this.dSRDate = dSRDate;
		this.hrsWorked = hrsWorked;
		this.dSRReport = dSRReport;
		this.submitDate = submitDate;
		this.visibility = visibility;
	}

	public DSR() {
		super();
	}
	
	
	
}
