package com.example.demo.service.workflow;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.repository.workflow.WFInstanceRepository;

@Service
@Transactional
public class WFInstanceServiceImpl implements WFInstanceService {

    @Autowired
    WFInstanceRepository wfInstanceRepository;
    
	@Override
	public WFInstance save(WFInstance wfInstance) {
		return wfInstanceRepository.save(wfInstance);
	}

	@Override
	public WFInstance findById(Long instanceId) throws BusinessException {
		return wfInstanceRepository.findById(instanceId).orElseThrow(() -> new BusinessException("WFInstance not found for instanceId: " + instanceId));
	}

}
