package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        user.setRoleId(1L);
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

    User getAdminUser() {
        User admin = new User();
        admin.setId(1L); 
        admin.setRoleId(RoleEnum.ADMIN.getCode());
        return admin;
    }

    User getSuperAdminUser() {
        User superAdmin = new User();
        superAdmin.setId(2L); 
        superAdmin.setRoleId(RoleEnum.SUPER_ADMIN.getCode());
        return superAdmin;
    }

    @Test
    public void getUserById() {
        Optional<User> autherParam = Optional.of(getUser());
        Mockito.when(userRepository.findById(0L)).thenReturn(autherParam);
        Optional<User> user = userService.getUserById(0L);
        assertEquals(true, user.isPresent());
    }

    @Test
    void testCreateUserSuccess() {
        User user = new User();
        user.setLoginId(2L); 
        user.setRoleId(RoleEnum.ADMIN.getCode());

        when(userRepository.findById(2L)).thenReturn(Optional.of(getSuperAdminUser()));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);
        assertEquals(user, createdUser);
    }

    @Test
    void testCreateUserUnauthorized() {
        User user = new User();
        user.setLoginId(1L); 
        user.setRoleId(RoleEnum.SUPER_ADMIN.getCode());

        when(userRepository.findById(1L)).thenReturn(Optional.of(getAdminUser()));

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
        customer.setId(10L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(getAdminUser())); 

        assertDoesNotThrow(() -> userService.deleteUser(1L, 10L));
    }

    @Test
    void testDeleteUserUnauthorized() {
        User customer = new User();
        customer.setRoleId(RoleEnum.SUPER_ADMIN.getCode());
        customer.setId(10L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(getAdminUser()));

        try {
            userService.deleteUser(1L, 10L);
        } catch (BusinessException exc) {
            assertEquals("Cannot delete this user: insufficient permissions", exc.getMessage());
        }
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.findById(10L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(getAdminUser()));

        try {
            userService.deleteUser(1L, 10L);
        } catch (BusinessException exc) {
            assertEquals("User not found", exc.getMessage());
        }
    }

    @Test
    public void getUserByIdNotFound() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> {
            userService.getUserById(1L);
        });
    }

    @Test
    public void updateUserHappyPath() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setLoginId(2L);
        updatedUser.setRoleId(4L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(getSuperAdminUser()));

        assertDoesNotThrow(() -> userService.updateUser(updatedUser));
        Optional<User> testUser = userRepository.findById(userId);
        assertEquals("Jane", testUser.get().getFirstName());
    }

    @Test
    public void updateUserNotFound() {
        Long userId = 1L;
        User updatedUser = new User();
        updatedUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            userService.updateUser(updatedUser);
        });
    }
}
