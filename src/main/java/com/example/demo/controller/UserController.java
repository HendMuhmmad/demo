package com.example.demo.controller;

import java.util.HashMap;
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

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
	this.userService = userService;
    }

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
	Map<String, String> response = new HashMap<>();
	try {
	    userService.createUser(UserMapper.INSTANCE.mapUserDto(userDto));
	    response.put("status", "Success");
	    response.put("message", "Created Successfully");
	    return new ResponseEntity<>(response, HttpStatus.CREATED);
	} catch (IllegalArgumentException exc) {
	    response.put("status", "Error");
	    response.put("message", exc.getMessage());
	    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}
    }

    @PutMapping("updateUser/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable int id, @RequestBody User user) {
	System.out.println("In Update User" + id);
	HashMap<String, String> response = new HashMap<>();

	try {
	    User updatedUser = userService.updateUser(id, user);
	    response.put("response", "User updated successfully.");
	    return ResponseEntity.ok(response);
	} catch (RuntimeException e) {
	    response.put("Response", "An Error Occurred.");
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

	}
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<Map<String, String>> deleteUser(
	    @RequestParam int loginId,
	    @RequestParam int customerId) {

	Map<String, String> response = new HashMap<>();
	try {
	    userService.deleteUser(loginId, customerId);
	    response.put("status", "Success");
	    response.put("message", "Deleted Successfully");
	    return new ResponseEntity<>(response, HttpStatus.OK);
	} catch (IllegalArgumentException e) {
	    response.put("status", "Error");
	    response.put("message", e.getMessage());
	    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	} catch (RuntimeException e) {
	    response.put("status", "Error");
	    response.put("message", "User not found");
	    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
    }
}
