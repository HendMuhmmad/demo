package com.example.demo.repository.workflow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.orm.workflow.WFProcess;

@Repository
public interface WFProcessRepository extends JpaRepository<WFProcess, Long> {

    @Modifying
    @Transactional
    @Query(value = "ALTER SEQUENCE ECO_PROCESSES_SEQ RESTART WITH ?1", nativeQuery = true)
    void resetSequence(long newValue);
    String findNameById(Long id);
}