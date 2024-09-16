package com.example.demo.model.orm;

 
import javax.persistence.*;

import lombok.Data;

@Entity
@Data
@Table(name = "Role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ROLE_DESC", nullable = false)
    private String roleDesc;

 }
