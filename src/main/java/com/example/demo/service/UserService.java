package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.example.demo.model.orm.User;

public interface UserService {
    public List<User> getAllUsers();

    public Optional<User> getUserById(int id);

    public User createUser(User user);

    public User updateUser(int id, User user);

    public  void deleteUser(int loginId, int customerId);
}
