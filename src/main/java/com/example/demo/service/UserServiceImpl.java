package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.orm.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
	this.userRepository = userRepository;
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
    public User createUser(User user) {
	Optional<User> loginUser = userRepository.findById((int) user.getLoginId());
	return userRepository.save(user);
    }

    @Override
    public User updateUser(int id, User user) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void deleteUser(int id) {
	// TODO Auto-generated method stub

    }

}
