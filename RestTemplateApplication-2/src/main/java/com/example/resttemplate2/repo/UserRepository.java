package com.example.resttemplate2.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.resttemplate2.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
