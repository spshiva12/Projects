package com.example.resttemplate2.userservice.serviceimpl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.resttemplate2.dto.DepartmentDto;
import com.example.resttemplate2.dto.ResponseDto;
import com.example.resttemplate2.dto.UserDto;
import com.example.resttemplate2.entity.User;
import com.example.resttemplate2.repo.UserRepository;
import com.example.resttemplate2.userservice.service.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class Userserviceimpl implements UserService {

	private UserRepository userRepo;
	private RestTemplate restTemplate;

	@Override
	public User saveUser(User user) {
		// TODO Auto-generated method stub
		return userRepo.save(user);
	}

	@Override
	public ResponseDto getUser(Long userId) {
		// TODO Auto-generated method stub

		ResponseDto responseDto = new ResponseDto();
		User user = userRepo.findById(userId).get();
		UserDto userDto = mapToUser(user);

		ResponseEntity<DepartmentDto> responseEntity = restTemplate
				.getForEntity("http://localhost:8080/api/departments/" + user.getId(), DepartmentDto.class);

		DepartmentDto departmentDto = responseEntity.getBody();

		System.out.println(responseEntity.getStatusCode());

		responseDto.setUser(userDto);
		responseDto.setDepartment(departmentDto);

		return responseDto;
	}

	private UserDto mapToUser(User user) {
		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setFirstName(user.getFirstName());
		userDto.setLastName(user.getLastName());
		userDto.setEmail(user.getEmail());
		return userDto;
	}

}
