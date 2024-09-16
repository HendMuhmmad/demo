package com.example.demo.model.orm;
import javax.persistence.*;

import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "First_Name", nullable = false)
    private String firstName;

    @Column(name = "Last_Name", nullable = false)
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Email", nullable = false, unique = true)
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

 }
