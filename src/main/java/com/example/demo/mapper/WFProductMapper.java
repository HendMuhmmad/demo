package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.demo.model.orm.WFProduct;
import com.example.demo.model.orm.Product;

@Mapper
public interface WFProductMapper {

    WFProductMapper INSTANCE = Mappers.getMapper(WFProductMapper.class);

 
    @Mapping(source = "productName", target = "productName")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "color", target = "color")
    @Mapping(source = "stockQuantity", target = "stockQuantity")
    @Mapping(source = "description", target = "description")
    @Mapping(target = "creationDate", ignore = true)  
    Product toProduct(WFProduct wfProduct);

    // Map Product to WFProduct
    @Mapping(source = "productName", target = "productName")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "color", target = "color")
    @Mapping(source = "stockQuantity", target = "stockQuantity")
    @Mapping(source = "description", target = "description")
    @Mapping(target = "wfInstanceId", ignore = true)  
    WFProduct toWFProduct(Product product);
}
