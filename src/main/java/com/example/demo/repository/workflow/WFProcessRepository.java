package com.example.demo.repository.workflow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.orm.workflow.WFProcess;

@Repository
public interface WFProcessRepository extends JpaRepository<WFProcess, Long> {
    String findNameById(Long id);
}