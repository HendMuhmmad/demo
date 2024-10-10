package com.example.demo.repository.workflow;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.workflow.WFTask;

public interface WFTaskRepository extends JpaRepository<WFTask, Long> {

    List<WFTask> findByInstanceIdOrderByIdAsc(long instanceId);

    // List<WFTask> findByInstanceIdAndAssigneeWfRoleOrderByIdAsc(long instanceId, String assigneeWFRole);

    public void deleteByInstanceId(Long instanceId);

    public WFTask findByInstanceId(Long instanceId);

    List<WFTask> findByAssigneeId(Long assigneeId);

    Optional<WFTask> findById(Long id);
}