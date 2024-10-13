package com.example.demo.service;
 

import java.util.List;
import java.util.Optional;

import com.example.demo.model.orm.workflow.ProductTrnsHistory;
 
 
public interface ProductTrnsHistoryService {
 
    List<ProductTrnsHistory> getAllTransactions();
 
    Optional<ProductTrnsHistory> getTransactionById(Long id);
 
    ProductTrnsHistory createTransaction(ProductTrnsHistory transaction);
 
    ProductTrnsHistory updateTransaction(Long id, ProductTrnsHistory updatedTransaction);
 
    void deleteTransaction(Long id);
}