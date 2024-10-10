package com.example.demo.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.dto.ProductUpdateStockQuantityDTO;
import com.example.demo.model.orm.OrderDetailsData;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.workflow.product.WFProduct;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    public ProductUpdateStockQuantityDTO mapUpdateProduct(Product product);

    public Product mapUpdateProduct(ProductUpdateStockQuantityDTO productdto);
    
    public ProductDto mapProduct(Product product);

	@Mapping(source="id", target="productId")
    public WFProduct mapProductDto(ProductDto productdto);
	
	@Mapping(source="productId", target="id")
    public Product mapWFProduct(WFProduct product);


    public List<ProductDto> mapProducts(List<Product> products);
    
	@Mapping(source="productColor", target="color")
	@Mapping(source="productDescription", target="description")
	@Mapping(source="totalPrice", target="price")
	@Mapping(target = "loginId", ignore = true)
    public ProductDto mapView(OrderDetailsData orderDetails); 
	
	default List<ProductDto> mapViews(List<OrderDetailsData> orderDetails)
	{
		return orderDetails.stream()
							.map(this::mapView)
							.collect(Collectors.toList());
	}
	

}
