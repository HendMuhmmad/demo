package com.example.demo.service.workflow;

import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFProcess;
import com.example.demo.model.orm.workflow.WFTask;

public interface BaseWFService {
    /*********************** WFProcess ****************************************/

    public WFProcess getWFProcessById(long id);

    public WFInstance getWFInstanceById(long id);

    public WFTask getWFTaskById(long id);

    public Long initWF(Long loginId, Long processId, Long assigneeId);

}
