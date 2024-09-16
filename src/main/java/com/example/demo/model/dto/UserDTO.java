package com.example.demo.model.dto;
 
import lombok.Data;
 
@Data
public class UserDTO {
 
    private String firstName;
    private String lastName;
    private int roleId;
 
    private String email;
    private String phone;
    private String nationality;
    private String gender;
    private int loginId;
 
}