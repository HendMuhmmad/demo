package com.example.demo.model.orm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ECO_USER")
public class User {
    @Id
    @SequenceGenerator(name = "ECO_USER_SEQ",
	    sequenceName = "ECO_USER_SEQ",
	    allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
	    generator = "ECO_USER_SEQ")
    private Long id;

    @Column(name = "First_Name",
	    nullable = false)
    private String firstName;

    @Column(name = "Last_Name",
	    nullable = false)
    private String lastName;

    @Column(name = "ROLE_ID",
	    nullable = false)
    private Long roleId;

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
    private Long loginId;

    public User(String firstName, String lastName, Long roleId, String password, String email, String address,
	    String phone, String nationality, String gender, Date registrationDate, Date birthday) {
	this.firstName = firstName;
	this.lastName = lastName;
	this.roleId = roleId;
	this.password = password;
	this.email = email;
	this.address = address;
	this.phone = phone;
	this.nationality = nationality;
	this.gender = gender;
	this.registrationDate = registrationDate;
	this.birthday = birthday;
    }

}
