package com.example.demo.service;

import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.ProductTranHistory;

public interface ProductTranHistoryService {
    public ProductTranHistory save(ProductTranHistory productTranHistory);

    public ProductTranHistory constructProductTransHisFromProduct(Product product);

}
