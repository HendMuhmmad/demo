package com.example.demo.repository.workflow;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.orm.workflow.WFTask;

public interface WFTaskRepository extends JpaRepository<WFTask, Long> {

    List<WFTask> findByInstanceIdOrderByIdAsc(long instanceId);

    // List<WFTask> findByInstanceIdAndAssigneeWfRoleOrderByIdAsc(long instanceId, String assigneeWFRole);

//    @Query(value = " select count(t.id) from WFTask t "
//	    + " where t.instanceId = :P_INSTANCE_ID "
//	    + " and t.action is NULL ")
//    Long countRunningTaskByInstanceId(@Param("P_INSTANCE_ID") Long instanceId);

//    public void deleteByInstanceId(Long instanceId);
//
//    public WFTask findByInstanceId(Long instanceId);

}