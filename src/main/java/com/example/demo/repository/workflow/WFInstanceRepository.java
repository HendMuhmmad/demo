package com.example.demo.repository.workflow;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.workflow.WFInstance;

public interface WFInstanceRepository extends JpaRepository<WFInstance, Long> {
    List<WFInstance> findByRequesterId(Long requesterId);

}