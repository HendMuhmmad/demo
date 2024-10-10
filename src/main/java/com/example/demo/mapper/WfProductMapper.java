package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.WfProductDto;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.WFProduct;

@Mapper
@Component
public interface WfProductMapper {

    WfProductMapper INSTANCE = Mappers.getMapper(WfProductMapper.class);

    public WFProduct mapWfProduct(WfProductDto wfProductDto);

    public WfProductDto maWfProductDto(WFProduct wfProduct);

    public Product maWfProductToProduct(WFProduct wfProduct);

}
