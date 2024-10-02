package com.example.demo.repository.workflow;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.orm.workflow.WFProduct;

@Repository
public interface WFProductRepository extends JpaRepository<WFProduct, Long> {
    Optional<WFProduct> findByWfInstanceId(Long wfInstanceId);

}