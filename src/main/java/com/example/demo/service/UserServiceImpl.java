package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	// return Optional.empty();
	return userRepository.findById(id);
    }

    @Override
	public void createUser(User user) {
        if (!isOperationAllowed(user.getLoginId(), user.getRoleId())) {
            throw new IllegalArgumentException("Cannot create this user");
        }
		
		userRepository.save(user);
	}
	private boolean isOperationAllowed(int creatorRoleId , int roleId) {
		if(creatorRoleId == 1 && roleId == 2) return true;
		else if(creatorRoleId == 2 && roleId == 3) return true;
		else if(creatorRoleId == 2 && roleId == 4) return true;
		else if(creatorRoleId == 3 && roleId == 4) return true;
		return false;
	}

	@Override
	public User updateUser(int id, User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUser(int loginId,int customerId) {
		User customer = userRepository.findById(customerId)
		            .orElseThrow(() -> new RuntimeException("User not found"));
		 
		int customerRoleId = customer.getRoleId();
		 
	    if (!isOperationAllowed(loginId, customerRoleId)) {
	        throw new IllegalArgumentException("Cannot delete this user: insufficient permissions");
	    }

	    userRepository.delete(customer);		
	}

}
