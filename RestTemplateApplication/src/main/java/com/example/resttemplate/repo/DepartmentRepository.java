package com.example.resttemplate.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.resttemplate.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
