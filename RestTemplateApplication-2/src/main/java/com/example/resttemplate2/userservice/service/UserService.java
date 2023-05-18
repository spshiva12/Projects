package com.example.resttemplate2.userservice.service;

import com.example.resttemplate2.dto.ResponseDto;
import com.example.resttemplate2.entity.User;

public interface UserService {

	User saveUser(User user);

	ResponseDto getUser(Long userId);

}
