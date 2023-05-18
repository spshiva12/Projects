package com.example.resttemplate2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDto {

	private Long id;
	private String departmentName;
	private String departmentAddress;
	private String departmentCode;

}
