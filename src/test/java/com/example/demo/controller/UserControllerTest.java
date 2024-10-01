package com.example.demo.controller;

import com.example.demo.model.orm.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        objectMapper = new ObjectMapper();
    }

    private User createUser(String firstName, String lastName, String email, Long roleId, String password) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setRoleId(roleId);
        user.setPassword(password);
        return userRepository.save(user);
    }

    @Test
    public void testGetAllUsersSuccess() throws Exception {
        createUser("John", "Doe", "john.doe@example.com", 2L, "securepassword");
        mockMvc.perform(get("/api/users")).andExpect(status().isOk());
    }

    @Test
    public void testGetUserByIdSuccess() throws Exception {
        User user = createUser("John", "Doe", "john.doe@example.com", 2L, "securepassword");
        mockMvc.perform(get("/api/users/getUserById/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    public void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/users/getUserById/{id}", 999))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUserSuccess() throws Exception {
        User admin = createUser("Admin", "Doe", "admin.doe@example.com", 2L, "securepassword");
        User user = createUser("John", "Doe", "john.doe@example.com", 4L, "securepassword");

        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setLoginId(admin.getId());
        updatedUser.setRoleId(4L);

        mockMvc.perform(post("/api/users/updateUser", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUnauthorized() throws Exception {
        User customerUser = createUser("Customer", "User", "customer.user@example.com", 4L, "anothersecurepassword");
        User user = createUser("John", "Doe", "john.doe@example.com", 4L, "securepassword");

        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setLoginId(customerUser.getId());
        updatedUser.setRoleId(4L);

        mockMvc.perform(post("/api/users/updateUser", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateUserSuccess() throws Exception {
        User headOfDepartment = createUser("John", "Doe", "john.doe@example.com", 1L, "securepassword");
        
        mockMvc.perform(post("/api/users/createUser")
                .contentType("application/json")
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"loginId\":"
                        + headOfDepartment.getId() + ",\"roleId\":2,\"password\":\"securepassword\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Created Successfully"));
    }

    @Test
    public void testCreateUserUnauthorized() throws Exception {
        User user = createUser("John", "Doe", "john.doe@example.com", 4L, "securepassword");

        mockMvc.perform(post("/api/users/createUser")
                .contentType("application/json")
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"loginId\":"
                        + user.getId() + ",\"roleId\":1,\"password\":\"securepassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("Error"))
                .andExpect(jsonPath("$.message").value("Cannot create this user"));
    }

    @Test
    public void testDeleteUserSuccess() throws Exception {
        User admin = createUser("Admin", "Doe", "admin.doe@example.com", 2L, "securepassword");
        User user = createUser("John", "Doe", "john.doe@example.com", 4L, "securepassword");

        mockMvc.perform(delete("/api/users/deleteUser")
                .param("loginId", String.valueOf(admin.getId()))
                .param("customerId", String.valueOf(user.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Deleted Successfully"));
    }

    @Test
    public void testDeleteUserUnauthorized() throws Exception {
        User admin = createUser("Admin", "Doe", "admin.doe@example.com", 2L, "securepassword");
        User user = createUser("John", "Doe", "john.doe@example.com", 1L, "securepassword");

        mockMvc.perform(delete("/api/users/deleteUser")
                .param("loginId", String.valueOf(admin.getId()))
                .param("customerId", String.valueOf(user.getId())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("Error"))
                .andExpect(jsonPath("$.message").value("Cannot delete this user: insufficient permissions"));
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {
        mockMvc.perform(delete("/api/users/deleteUser")
                .param("loginId", "1")
                .param("customerId", "999"))
                .andExpect(jsonPath("$.status").value("Error"))
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}
