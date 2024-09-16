package com.example.demo.model.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

	private int id;
    private String firstName;
    private String lastName;
    private int roleId;
    private String password;
    private String email;
    private String address;
    private String phone;
    private String nationality;
    private String gender;
    private Date registrationDate;
    private Date birthday;
    private long loginId;

}
