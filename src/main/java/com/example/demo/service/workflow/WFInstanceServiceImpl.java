package com.example.demo.service.workflow;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.repository.workflow.WFInstanceRepository;

@Service
@Transactional
public class WFInstanceServiceImpl implements WFInstanceService {

    @Autowired
    private WFInstanceRepository wfInstanceRepository;

    /*---------------------- Queries -----------------------------------------*/

    @Override
    public WFInstance getWFInstanceById(long id) {
	return wfInstanceRepository.findById(id)
		.orElseThrow(() -> new EntityNotFoundException("Instance not found with ID: " + id));
    }

    @Override
    public Long save(WFInstance wfInstance) {
	return wfInstanceRepository.save(wfInstance).getId();
    }
}