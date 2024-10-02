package com.example.demo.model.orm;

 
import javax.persistence.*;

import lombok.Data;

@Entity
@Data
@Table(name = "eco_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ROLE_DESC", nullable = false)
    private String roleDesc;

 }
