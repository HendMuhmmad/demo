package com.example.demo.service.workflow;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.orm.workflow.WFProcess;
import com.example.demo.repository.workflow.WFProcessRepository;

@Service
@Transactional
public class WFProcessServiceImpl implements WFProcessService {

    private WFProcessRepository wfProcessRepository;

    @Autowired
    public WFProcessServiceImpl(WFProcessRepository wfProcessRepository) {
	this.wfProcessRepository = wfProcessRepository;
    }

    @Override
    public WFProcess getWFProcessById(long processId) {
	return wfProcessRepository.findById(processId)
		.orElseThrow(() -> new EntityNotFoundException("Process not found with ID: " + processId));
    }
}