package com.example.demo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.enums.RoleEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.User;
import com.example.demo.repository.UserRepository;

@SpringBootTest
public class UserServiceTest {

    @MockBean
    UserRepository userRepository;

    @Autowired
    private UserService userService;

    User getUser() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRoleId(1);
        user.setPassword("securepassword");
        user.setEmail("john.doe@example.com");
        user.setAddress("123 Main St");
        user.setPhone("123-456-7890");
        user.setNationality("American");
        user.setGender("Male");
        user.setRegistrationDate(new Date());
        user.setBirthday(new Date(1990, 1, 1));
        return user;
    }

    @Test
    public void getUserById() {
        Optional<User> autherParam = Optional.of(getUser());
        Mockito.when(userRepository.findById(0)).thenReturn(autherParam);
        Optional<User> user = userService.getUserById(0);
        assertEquals(true, user.isPresent());
    }

    @Test
    void testCreateUserSuccess() {
        User user = new User();
        user.setLoginId(RoleEnum.SUPER_ADMIN.getCode());
        user.setRoleId(RoleEnum.ADMIN.getCode());

        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);
        assertEquals(user, createdUser);
    }

    @Test
    void testCreateUserUnauthorized() {
        User user = new User();
        user.setLoginId(RoleEnum.ADMIN.getCode());
        user.setRoleId(RoleEnum.SUPER_ADMIN.getCode());

        try {
            userService.createUser(user);
        } catch (BusinessException exc) {
            assertEquals("Cannot create this user", exc.getMessage());
        }
    }

    @Test
    void testDeleteUserSuccess() {
        User customer = new User();
        customer.setRoleId(RoleEnum.CUSTOMER.getCode());
        customer.setId(10);

        when(userRepository.findById(10)).thenReturn(Optional.of(customer));

        assertDoesNotThrow(() -> userService.deleteUser(RoleEnum.ADMIN.getCode(), 10));
    }

    @Test
    void testDeleteUserUnauthorized() {
        User customer = new User();
        customer.setRoleId(RoleEnum.SUPER_ADMIN.getCode());
        customer.setId(10);

        when(userRepository.findById(10)).thenReturn(Optional.of(customer));

        try {
            userService.deleteUser(RoleEnum.ADMIN.getCode(), 10);
        } catch (BusinessException exc) {
            assertEquals("Cannot delete this user: insufficient permissions", exc.getMessage());
        }
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.findById(10)).thenReturn(Optional.empty());

        try {
            userService.deleteUser(RoleEnum.ADMIN.getCode(), 10);
        } catch (BusinessException exc) {
            assertEquals("User not found", exc.getMessage());
        }
    }

 
    @Test
    public void getUserByIdNotFound() {
        // Mock repository behavior to return an empty Optional for user ID 1
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Assert that a BusinessException is thrown when trying to get a user by ID 1
         assertThrows(BusinessException.class, () -> {
            userService.getUserById(1);
        });

        // Verify the exception message
     }

 

    @Test
    public void updateUserHappyPath() {
        int userId = 1;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");

        User updatedUser = new User();
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Ensure the updateUser method does not throw any exceptions
        User result = assertDoesNotThrow(() -> userService.updateUser(userId, updatedUser));

         
    }

    @Test
    public void updateUserNotFound() {
        int userId = 1;
        User updatedUser = new User();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        try {
            userService.updateUser(userId, updatedUser);
        } catch (BusinessException exc) {
            assertEquals("User with ID " + userId + " not found.", exc.getMessage());
        }
    }
}
