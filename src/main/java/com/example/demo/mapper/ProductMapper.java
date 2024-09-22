package com.example.demo.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.dto.ProductUpdateDto;
import com.example.demo.model.dto.ProductUpdateStockQuantityDTO;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.Vw_Order_Details;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    public ProductUpdateStockQuantityDTO mapUpdateProduct(Product product);

    public Product mapUpdateProduct(ProductUpdateStockQuantityDTO productdto);

    public Product mapUpdateProduct(ProductUpdateDto productdto);
    
    public ProductDto mapCreateProduct(Product product);

    public Product mapCreateProduct(ProductDto productdto);

    public List<ProductDto> mapProducts(List<Product> products);
    
	@Mapping(source="productColor", target="color")
	@Mapping(source="productDescription", target="description")
	@Mapping(source="totalPrice", target="price")
	@Mapping(target = "loginId", ignore = true)
    public ProductDto mapView(Vw_Order_Details orderDetails); 
	
	default List<ProductDto> mapViews(List<Vw_Order_Details> orderDetails)
	{
		return orderDetails.stream()
							.map(this::mapView)
							.collect(Collectors.toList());
	}
	

}
