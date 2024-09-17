package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.enumeration.UserEnum;
import com.example.demo.model.orm.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository,RoleRepository roleRepository) {
        this.userRepository = userRepository;
		this.roleRepository = roleRepository;
    }
    @Override
    public List<User> getAllUsers() {

	return userRepository.findAll();
    }

 	@Override
	public Optional<User> getUserById(int id) {
		// TODO Auto-generated method stub
        return userRepository.findById(id);

	}
 

    @Override
	public void createUser(User user) {
        UserEnum creatorType = UserEnum.fromRoleId(user.getLoginId());
        UserEnum userType = UserEnum.fromRoleId(user.getRoleId());
        
        if (!isOperationAllowed(creatorType, userType)) {
            throw new IllegalArgumentException("Cannot create this user");
        }
		
		userRepository.save(user);
	}
    private boolean isOperationAllowed(UserEnum creatorType, UserEnum userType) {
        if (creatorType == UserEnum.HEAD_OF_DEPARTMENT && userType == UserEnum.SUPER_ADMIN) {
            return true;
        }
        if (creatorType == UserEnum.SUPER_ADMIN && (userType == UserEnum.ADMIN || userType == UserEnum.CUSTOMER)) {
            return true;
        }
        if (creatorType == UserEnum.ADMIN && userType == UserEnum.CUSTOMER) {
            return true;
        }

        return false;
    }

	@Override
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
		    throw new EntityNotFoundException("User with ID " + id + " not found.");
		}
	}

	@Override
	public void deleteUser(int loginId,int customerId) {
		User customer = userRepository.findById(customerId)
		            .orElseThrow(() -> new RuntimeException("User not found"));
		 
		UserEnum creatorType = UserEnum.fromRoleId(loginId);
        UserEnum customerType = UserEnum.fromRoleId(customer.getRoleId());
	    if (!isOperationAllowed(creatorType, customerType)) {
	        throw new IllegalArgumentException("Cannot delete this user: insufficient permissions");
	    }

	    userRepository.delete(customer);		
	}

}
