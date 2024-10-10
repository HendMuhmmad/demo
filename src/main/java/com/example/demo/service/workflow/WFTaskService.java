package com.example.demo.service.workflow;

import java.util.List;

import com.example.demo.model.dto.workflow.WFTaskDetailsDto;
import com.example.demo.model.orm.workflow.WFTask;

public interface WFTaskService {

    /*********************** WFTask ********************************************/

    /*---------------------- Queries -----------------------------------------*/

    public WFTask getWFTaskById(long id);

    public List<WFTaskDetailsDto<?>> getProductWfTasks(Long userId);

    public WFTaskDetailsDto<?> getTaskById(Long taskId);

    public WFTask save(WFTask wfTask);

}