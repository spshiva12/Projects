package com.ojas.timesheet.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ojas.timesheet.entity.Projects;

@Repository
public interface ProjectsRepo extends JpaRepository<Projects, Long>{

	@Query(value = "select distinct(project_name) from projects" , nativeQuery = true)
	List<String> getAllProjectsName();

}
