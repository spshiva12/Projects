package com.example.resttemplate.service;

import com.example.resttemplate.entity.Department;

public interface DepartmentService {

	Department saveDepartment(Department department);
	
	Department getDepartmentById(Long departmentId);
}
