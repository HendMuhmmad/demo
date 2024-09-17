package com.example.demo.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.dto.ProductUpdateStockQuantityDTO;
import com.example.demo.model.orm.Product;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    public ProductUpdateStockQuantityDTO mapUpdateProduct(Product product);

    public Product mapUpdateProduct(ProductUpdateStockQuantityDTO productdto);

    public ProductDto mapCreateProduct(Product product);

    public Product mapCreateProduct(ProductDto productdto);

    public List<ProductDto> mapProducts(List<Product> products);

}
