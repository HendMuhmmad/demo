package com.example.demo.repository.workflow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.orm.workflow.WFActions;

public interface WFActionsRepository  extends JpaRepository<WFActions, Long>{

    @Modifying
    @Transactional
    @Query(value = "ALTER SEQUENCE ECO_WF_ACTIONS_SEQ RESTART WITH ?1", nativeQuery = true)
    void resetSequence(long newValue);

}
