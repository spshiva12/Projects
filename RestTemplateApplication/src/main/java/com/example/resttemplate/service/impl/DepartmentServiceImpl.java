package com.example.resttemplate.service.impl;

import org.springframework.stereotype.Service;

import com.example.resttemplate.entity.Department;
import com.example.resttemplate.repo.DepartmentRepository;
import com.example.resttemplate.service.DepartmentService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

	private DepartmentRepository departmentRepo;

	@Override
	public Department saveDepartment(Department department) {
		// TODO Auto-generated method stub
		return departmentRepo.save(department);
	}

	@Override
	public Department getDepartmentById(Long departmentId) {
		// TODO Auto-generated method stub
		return departmentRepo.findById(departmentId).get();
	}

}
