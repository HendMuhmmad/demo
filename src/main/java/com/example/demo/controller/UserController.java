package com.example.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.orm.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
	List<User> users = userService.getAllUsers();
	return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("getUserById/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable int id) {
	Optional<User> user = userService.getUserById(id);

	return user.map(u -> ResponseEntity.ok(UserMapper.INSTANCE.mapUser(u)))
		.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/createUser")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody UserDto userDto) {
	return userService.createUser(UserMapper.INSTANCE.mapUserDto(userDto));
    }

    @PutMapping("updateUser/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable int id, @RequestBody User user) {
	return userService.updateUser(id, user);
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<Map<String, String>> deleteUser(
	    @RequestParam int loginId,
	    @RequestParam int customerId) {
	return userService.deleteUser(loginId, customerId);
    }
}
