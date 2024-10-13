package com.example.demo.controller;

 

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.orm.ProductTrnsHistory;
import com.example.demo.service.ProductTrnsHistoryServiceImpl;

@RestController
@RequestMapping("/api/transactions")
public class ProductTrnsHistoryController {

    @Autowired
    private ProductTrnsHistoryServiceImpl service;

    @GetMapping
    public List<ProductTrnsHistory> getAllTransactions() {
        return service.getAllTransactions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductTrnsHistory> getTransactionById(@PathVariable Long id) {
        Optional<ProductTrnsHistory> transaction = service.getTransactionById(id);
        return transaction.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ProductTrnsHistory createTransaction(@RequestBody ProductTrnsHistory transaction) {
        return service.createTransaction(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductTrnsHistory> updateTransaction(@PathVariable Long id, @RequestBody ProductTrnsHistory transaction) {
        try {
            ProductTrnsHistory updatedTransaction = service.updateTransaction(id, transaction);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        service.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
