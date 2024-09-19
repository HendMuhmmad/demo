package com.example.demo.service;
import static org.junit.Assert.assertEquals;

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

import com.example.demo.model.orm.User;
import com.example.demo.repository.UserRepository;
	
@SpringBootTest
public class UserServiceTest {
	@MockBean
	UserRepository userRepository;
	
	@Autowired
	UserService userService;
	
	User getUser() {
		   User user = new User();

	        // Set the properties of the user
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
	        user.setBirthday(new Date(1990, 1, 1)); // Example birthday

	        // Optionally, set a transient field (this won't persist to the database)
	        user.setLoginId(101);
	        return user;
	}
	
    @Test
    public void getUserById() {
		Optional<User> autherParam = Optional.of(getUser());
		System.out.println("Testing....");
		System.out.print(getUser());
		
		Mockito.when(userRepository.findById(0)).thenReturn(autherParam);
		
		Optional<User> user = userService.getUserById(0);
		assertEquals(true, user.isPresent());

    }
    
    @Test
    public void getUserByIdNotFound() {
		Optional<User> autherParam = Optional.of(getUser());
		System.out.println("Testing....");
		System.out.print(getUser());
		
		Mockito.when(userRepository.findById(0)).thenReturn(autherParam);
		
		Optional<User> user = userService.getUserById(1);
		assertEquals(true, !user.isPresent());

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

         ResponseEntity<Map<String, String>> response = userService.updateUser(userId, updatedUser);

  
         assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User updated successfully.", response.getBody().get("response"));
    
    }
    
    @Test
    public void updateUserNotFound() {
        // Mock input data
        int userId = 1;
        User updatedUser = new User();

        // Mock repository behavior
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Execute the method to be tested
        ResponseEntity<Map<String, String>> response = userService.updateUser(userId, updatedUser);

     

        // Validate the result
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
     }

    
    
}
 

 
