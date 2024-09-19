package com.example.demo.service;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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

		Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(autherParam);
			
		Optional<User> user = userService.getUserById(1);
		
		assertEquals(true, user.isPresent());
//		assertEquals("ali@gmail.com", user.get().getId());
	

    }
}
 

 
