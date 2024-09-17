package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.demo.model.dto.ProductDTO;
import com.example.demo.model.dto.ProductUpdateStockQuantityDTO;
import com.example.demo.model.orm.Product;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    public ProductUpdateStockQuantityDTO mapUpdateProduct(Product product);

    public Product mapUpdateProduct(ProductUpdateStockQuantityDTO productdto);

    public ProductDTO mapCreateProduct(Product product);

    public Product mapCreateProduct(ProductDTO productdto);

}
