package com.example.demo.repository.workflow;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.workflow.WFInstance;

public interface WFInstanceRepository extends JpaRepository<WFInstance, Long> {

}