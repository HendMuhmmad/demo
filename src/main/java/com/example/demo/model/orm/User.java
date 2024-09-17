package com.example.demo.model.orm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Data
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "First_Name",
	    nullable = false)
    private String firstName;

    @Column(name = "Last_Name",
	    nullable = false)
    private String lastName;

    @Column(name = "ROLE_ID",
	    nullable = false)
    private int roleId;

    @Column(name = "Password",
	    nullable = false)
    private String password;

    @Column(name = "Email",
	    nullable = false,
	    unique = true)
    private String email;

    @Column(name = "Address")
    private String address;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Nationality")
    private String nationality;

    @Column(name = "Gender")
    private String gender;

    @Column(name = "Registration_Date")
    @Temporal(TemporalType.DATE)
    private Date registrationDate;

    @Column(name = "Birthday")
    @Temporal(TemporalType.DATE)
    private Date birthday;
    
    @Transient
    private int loginId;

}
