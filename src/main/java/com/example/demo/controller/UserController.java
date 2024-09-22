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

import com.example.demo.exception.BusinessException;
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
        Map<String, String> response = new HashMap<>();
        try {
            User user = userService.createUser(UserMapper.INSTANCE.mapUserDto(userDto));
            response.put("status", "Success");
            response.put("message", "Created Successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (BusinessException exc) {
            response.put("status", "Error");
            response.put("message", exc.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // Adjust the status as needed
        }
    }


    @PostMapping("/updateUser/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable int id, @RequestBody UserDto userDto) {
        Map<String, String> response = new HashMap<>();
        try {
            User updatedUser = userService.updateUser(id, UserMapper.INSTANCE.mapUserDto(userDto));
            response.put("status", "Success");
            response.put("message", "User updated successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException exc) {
            response.put("status", "Error");
            response.put("message", exc.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // Adjust the status as needed
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
        } catch (BusinessException exc) {
            response.put("status", "Error");
            response.put("message", exc.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // Adjust the status as needed
        }
    }

}
