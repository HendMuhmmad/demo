package com.example.demo.repository.workflow;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.orm.ProductTransaction;

@Repository
public interface ProductTransactionRepository extends JpaRepository<ProductTransaction, Long> {

    public List<ProductTransaction> findAllByWfInstanceId(Long wfInstanceId);

    public ProductTransaction findWfProductByWfInstanceId(Long wfInstanceId);

}
