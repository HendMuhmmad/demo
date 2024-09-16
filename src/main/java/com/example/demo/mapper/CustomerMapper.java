package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.demo.model.dto.user.CustomerDTO;
import com.example.demo.model.orm.User;

@Mapper(componentModel = "spring")
public abstract class CustomerMapper {

   public static final CustomerMapper MAPPER  = Mappers.getMapper(CustomerMapper.class);

    public CustomerDTO mapCustomer(User user) {
    	CustomerDTO customerDTO = new CustomerDTO();
    	customerDTO.setName(user.getFirstName() + " " + user.getLastName());
    	customerDTO.setAddress(user.getAddress());
    	customerDTO.setPhoneNumber(user.getPhone());
    	return customerDTO;
    }

//    public User mapCustomerDto(CustomerDTO userDto) {
//    	return null;
//    }

}
