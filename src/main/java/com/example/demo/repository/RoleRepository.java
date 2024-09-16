package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Custom query methods can be added here if needed
}
