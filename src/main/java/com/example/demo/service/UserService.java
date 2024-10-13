package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.orm.User;

public interface UserService {
    public List<User> getAllUsers();

    public Optional<User> getUserById(Long id);

    public User createUser(User user);

    public User updateUser(User user);

    public List<User> getUsesrByRole(long roleId);
    
    public void deleteUser(Long loginId, Long customerId);
    
}
