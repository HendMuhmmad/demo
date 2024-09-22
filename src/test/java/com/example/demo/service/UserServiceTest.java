package com.example.demo.service;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    user.setId(0);
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
    Optional < User > autherParam = Optional.of(getUser());

    Mockito.when(userRepository.findById(0)).thenReturn(autherParam);

    Optional < User > user = userService.getUserById(0);

    assertEquals(true, user.isPresent());

  }

  @Test
  void testCreateUserSuccess() {
    User user = new User();
    user.setLoginId(RoleEnum.SUPER_ADMIN.getCode());
    user.setRoleId(RoleEnum.ADMIN.getCode());

    when(userRepository.save(any(User.class))).thenReturn(user);

    ResponseEntity < Map < String, String >> response = userService.createUser(user);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Success", response.getBody().get("status"));
    assertEquals("Created Successfully", response.getBody().get("message"));
  }

  @Test
  void testCreateUserUnauthorized() {
    User user = new User();
    user.setLoginId(RoleEnum.ADMIN.getCode());
    user.setRoleId(RoleEnum.SUPER_ADMIN.getCode());

    ResponseEntity < Map < String, String >> response = userService.createUser(user);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Error", response.getBody().get("status"));
    assertEquals("Cannot create this user", response.getBody().get("message"));
  }

  @Test
  void testDeleteUserSuccess() {
    User customer = new User();
    customer.setRoleId(RoleEnum.CUSTOMER.getCode());
    customer.setId(10);

    when(userRepository.findById(10)).thenReturn(Optional.of(customer));

    ResponseEntity < Map < String, String >> response = userService.deleteUser(RoleEnum.ADMIN.getCode(), 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Success", response.getBody().get("status"));
    assertEquals("Deleted Successfully", response.getBody().get("message"));
  }

  @Test
  void testDeleteUserUnauthorized() {
    User customer = new User();
    customer.setRoleId(RoleEnum.SUPER_ADMIN.getCode());
    customer.setId(10);

    when(userRepository.findById(10)).thenReturn(Optional.of(customer));

    ResponseEntity < Map < String, String >> response = userService.deleteUser(RoleEnum.ADMIN.getCode(), 10);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Error", response.getBody().get("status"));
    assertEquals("Cannot delete this user: insufficient permissions", response.getBody().get("message"));
  }

  @Test
  void testDeleteUserNotFound() {
    when(userRepository.findById(10)).thenReturn(Optional.empty());

    ResponseEntity < Map < String, String >> response = userService.deleteUser(RoleEnum.ADMIN.getCode(), 10);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error", response.getBody().get("status"));
    assertEquals("User not found", response.getBody().get("message"));
  }

  @Test
  public void getUserByIdNotFound() {
    Optional < User > autherParam = Optional.of(getUser());

    Mockito.when(userRepository.findById(0)).thenReturn(autherParam);

    BusinessException exception = assertThrows(BusinessException.class, () -> {
      userService.getUserById(1);
    });

    assertEquals("Cannot find user in DB", exception.getMessage());

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
    updatedUser.setId(userId);
    Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

    ResponseEntity < Map < String, String >> response = userService.updateUser(userId, updatedUser);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("User updated successfully.", response.getBody().get("response"));

  }

  @Test
  public void updateUserNotFound() {
    // Mock input data

    User updatedUser = new User();
    int loginId = updatedUser.getId();
    // Mock repository behavior
    Mockito.when(userRepository.findById(updatedUser.getId())).thenReturn(Optional.empty());

    // Execute the method to be tested
    ResponseEntity < Map < String, String >> response = userService.updateUser(loginId, updatedUser);

    // Validate the result
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
  @Test
  public void updateAnotherUser() {
    // Mock input data

    User updatedUser = new User();
    int loginId = 200;
    // Mock repository behavior
    Mockito.when(userRepository.findById(updatedUser.getId())).thenReturn(Optional.empty());

    // Execute the method to be tested
    ResponseEntity < Map < String, String >> response = userService.updateUser(loginId, updatedUser);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

  }

}