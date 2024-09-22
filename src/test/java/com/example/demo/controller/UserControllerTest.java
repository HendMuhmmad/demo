package com.example.demo.controller;

import com.example.demo.model.orm.User;
import com.example.demo.repository.UserRepository;
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

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void testCreateUserSuccess() throws Exception {
        mockMvc.perform(post("/api/users/createUser")
                .contentType("application/json")
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"loginId\":1,\"roleId\":2,\"password\":\"securepassword\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Created Successfully"));
    }

    @Test
    public void testCreateUserUnauthorized() throws Exception {
        mockMvc.perform(post("/api/users/createUser")
                .contentType("application/json")
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"loginId\":2,\"roleId\":1,\"password\":\"securepassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("Error"))
                .andExpect(jsonPath("$.message").value("Cannot create this user"));
    }

    @Test
    public void testDeleteUserSuccess() throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setRoleId(2);
        user.setPassword("securepassword");
        userRepository.save(user);

        mockMvc.perform(delete("/api/users/deleteUser")
                .param("loginId", "1")
                .param("customerId", String.valueOf(user.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Deleted Successfully"));
    }

    @Test
    public void testDeleteUserUnauthorized() throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setRoleId(1);
        user.setPassword("securepassword");
        userRepository.save(user);

        mockMvc.perform(delete("/api/users/deleteUser")
                .param("loginId", "2")
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
