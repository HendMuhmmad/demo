package com.example.demo.service.workflow;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.WFProduct;

public interface WFProductService {
    public WFProduct findByWfInstanceId(Long wfInstanceId);

    public void init(Product product, Long loginId) throws BusinessException;

    public void initWf(WFProduct wfProduct, Long loginId) throws BusinessException;

    public WFProduct save(WFProduct wfProduct);

    public ResponseEntity<String> doAction(Long taskId, Long actionId);

    public List<WFProduct> getRunningRequests(Long productId);

}
