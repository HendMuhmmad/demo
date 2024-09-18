package com.example.demo.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.dto.ProductUpdateDto;
import com.example.demo.model.dto.ProductUpdateStockQuantityDTO;
import com.example.demo.model.orm.OrderDetails;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.Vw_Order_Details;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    public ProductUpdateStockQuantityDTO mapUpdateProduct(Product product);

    public Product mapUpdateProduct(ProductUpdateStockQuantityDTO productdto);

    public Product mapUpdateProduct(ProductUpdateDto productdto);
    
    public ProductDto mapCreateProduct(Product product);

    @Mapping(source="product.color", target="color")
	@Mapping(source="product.description", target="description")
	@Mapping(source="product.price", target="price")
	@Mapping(source="product.productName", target="productName")
	@Mapping(source="product.stockQuantity", target="stockQuantity")
    @Mapping(expression="java(orderDetails.getQuantity())", target="orderedQuantity" )
	@Mapping(target = "loginId", ignore = true)
    public ProductDto mapProductAndOrderDetails(Product product, @Context OrderDetails orderDetails);
    
    
    public Product mapCreateProduct(ProductDto productdto);
    
	@Mapping(source="productColor", target="color")
	@Mapping(source="productDescription", target="description")
	@Mapping(source="productPrice", target="price")
	@Mapping(target = "loginId", ignore = true)
    public ProductDto mapView(Vw_Order_Details orderDetails); 
	
	default List<ProductDto> mapViews(List<Vw_Order_Details> orderDetails)
	{
		return orderDetails.stream()
							.map(this::mapView)
							.collect(Collectors.toList());
	}
    public List<ProductDto> mapProducts(List<Product> products);

}
