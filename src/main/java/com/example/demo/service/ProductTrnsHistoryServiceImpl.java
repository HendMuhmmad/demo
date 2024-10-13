package com.example.demo.service;
 
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.orm.workflow.ProductTrnsHistory;
import com.example.demo.repository.workflow.ProductTrnsHistoryRepository;
 

 
@Service

public class ProductTrnsHistoryServiceImpl implements ProductTrnsHistoryService {
 
    @Autowired

    private ProductTrnsHistoryRepository repository;
 
    @Override

    public List<ProductTrnsHistory> getAllTransactions() {

        return repository.findAll();

    }
 
    @Override

    public Optional<ProductTrnsHistory> getTransactionById(Long id) {

        return repository.findById(id);

    }
 
    @Override

    public ProductTrnsHistory createTransaction(ProductTrnsHistory transaction) {

        return repository.save(transaction);

    }
 
    @Override

    public ProductTrnsHistory updateTransaction(Long id, ProductTrnsHistory updatedTransaction) {

        return repository.findById(id)

            .map(transaction -> {

                transaction.setProductId(updatedTransaction.getProductId());

                transaction.setProductName(updatedTransaction.getProductName());

                transaction.setPrice(updatedTransaction.getPrice());

                transaction.setColor(updatedTransaction.getColor());

                transaction.setStockQuantity(updatedTransaction.getStockQuantity());

                transaction.setDescription(updatedTransaction.getDescription());

                transaction.setCreationDate(updatedTransaction.getCreationDate());

                transaction.setStatus(updatedTransaction.getStatus());

                return repository.save(transaction);

            })

            .orElseThrow(() -> new RuntimeException("Transaction not found with id " + id));

    }
 
    @Override

    public void deleteTransaction(Long id) {

        repository.deleteById(id);

    }

}

 