package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.orm.User;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserById(int id);

    void createUser(User user);

    User updateUser(int id, User user);

    void deleteUser(int loginId,int customerId);
}
