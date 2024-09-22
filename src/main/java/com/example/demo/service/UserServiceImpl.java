package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.RoleEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.User;
import com.example.demo.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {

	return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(int id) {
	return Optional.of(userRepository.findById(id)
		.orElseThrow(() -> new BusinessException("Cannot find user in DB")));

    }

    @Override
    public ResponseEntity<Map<String, String>> createUser(User user) {
	Map<String, String> response = new HashMap<>();
	try {
	    if (!isOperationAllowed(user.getLoginId(), user.getRoleId())) {
		response.put("status", "Error");
		response.put("message", "Cannot create this user");
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	    }

	    userRepository.save(user);
	    response.put("status", "Success");
	    response.put("message", "Created Successfully");
	    return new ResponseEntity<>(response, HttpStatus.CREATED);
	} catch (Exception exc) {
	    response.put("status", "Error");
	    response.put("message", exc.getMessage());
	    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
    }

    public boolean isOperationAllowed(int creatorRoleId, int roleId) {
	if (creatorRoleId == RoleEnum.HEAD_OF_DEPARTMENT.getCode() && roleId == RoleEnum.SUPER_ADMIN.getCode())
	    return true;
	else if (creatorRoleId == RoleEnum.SUPER_ADMIN.getCode() && roleId == RoleEnum.ADMIN.getCode())
	    return true;
	else if (creatorRoleId == RoleEnum.SUPER_ADMIN.getCode() && roleId == RoleEnum.CUSTOMER.getCode())
	    return true;
	else if (creatorRoleId == RoleEnum.ADMIN.getCode() && roleId == RoleEnum.CUSTOMER.getCode())
	    return true;
	return false;
    }

    @Override
    public ResponseEntity<Map<String, String>> updateUser(int id, User updatedUser) {
    	 
	Map<String, String> response = new HashMap<>();
	try {
	    Optional<User> existingUserOptional = userRepository.findById(id);

	    if (existingUserOptional.isPresent()) {
		User existingUser = existingUserOptional.get();

		if (updatedUser.getFirstName() != null) {
		    existingUser.setFirstName(updatedUser.getFirstName());
		}
		if (updatedUser.getLastName() != null) {
		    existingUser.setLastName(updatedUser.getLastName());
		}
		if (updatedUser.getEmail() != null) {
		    existingUser.setEmail(updatedUser.getEmail());
		}
		if (updatedUser.getAddress() != null) {
		    existingUser.setAddress(updatedUser.getAddress());
		}
		if (updatedUser.getPhone() != null) {
		    existingUser.setPhone(updatedUser.getPhone());
		}
		if (updatedUser.getNationality() != null) {
		    existingUser.setNationality(updatedUser.getNationality());
		}
		if (updatedUser.getGender() != null) {
		    existingUser.setGender(updatedUser.getGender());
		}
		if (updatedUser.getRegistrationDate() != null) {
		    existingUser.setRegistrationDate(updatedUser.getRegistrationDate());
		}
		if (updatedUser.getBirthday() != null) {
		    existingUser.setBirthday(updatedUser.getBirthday());
		}

		userRepository.save(existingUser);
		response.put("response", "User updated successfully.");
		return new ResponseEntity<>(response, HttpStatus.OK);
	    } else {
		response.put("response", "User with ID " + id + " not found.");
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	    }
	} catch (Exception e) {
	    response.put("response", "An error occurred: " + e.getMessage());
	    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
    }

    public ResponseEntity<Map<String, String>> deleteUser(int loginId, int customerId) {
	Map<String, String> response = new HashMap<>();
	try {
	    // Check if user exists
	    User customer = userRepository.findById(customerId)
		    .orElseThrow(() -> new RuntimeException("User not found"));

	    // Check if operation is allowed
	    if (!isOperationAllowed(loginId, customer.getRoleId())) {
		response.put("status", "Error");
		response.put("message", "Cannot delete this user: insufficient permissions");
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	    }

	    userRepository.delete(customer);
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
	} catch (Exception e) {
	    response.put("status", "Error");
	    response.put("message", "An unexpected error occurred: " + e.getMessage());
	    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
    }

}
