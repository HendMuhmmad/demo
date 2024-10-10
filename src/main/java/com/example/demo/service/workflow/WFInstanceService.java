package com.example.demo.service.workflow;

import com.example.demo.model.orm.workflow.WFInstance;

public interface WFInstanceService {

    /*********************** WFInstance ***************************************/

    /*---------------------- Queries -----------------------------------------*/

    public WFInstance getWFInstanceById(long id);

    public Long save(WFInstance wfInstance);

}