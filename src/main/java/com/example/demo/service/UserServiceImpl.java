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

    public User createUser(User user) {
        if (!isOperationAllowed(user.getLoginId(), user.getRoleId())) {
            throw new BusinessException("Cannot create this user");
        }

        try {
            return userRepository.save(user);
        } catch (Exception exc) {
            throw new BusinessException(exc.getMessage());
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

    public User updateUser(int id, User updatedUser) {
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

            return userRepository.save(existingUser);
        } else {
            throw new BusinessException("User with ID " + id + " not found.");
        }
    }

    public void deleteUser(int loginId, int customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("User not found"));

        if (!isOperationAllowed(loginId, customer.getRoleId())) {
            throw new BusinessException("Cannot delete this user: insufficient permissions");
        }

        userRepository.delete(customer);
    }
}
