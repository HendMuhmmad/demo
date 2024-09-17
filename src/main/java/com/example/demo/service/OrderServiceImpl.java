package com.example.demo.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.CustomerDto;
import com.example.demo.model.dto.OrderResponseDto;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.orm.VW_ORDER_DETAILS;
import com.example.demo.repository.VWOrderDetailsRepository;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private VWOrderDetailsRepository orderDetailsViewRepository;

    public OrderResponseDto getOrderDetails(String orderNumber) {
        List<VW_ORDER_DETAILS> orderDetailsList = orderDetailsViewRepository.findByOrderNumber(orderNumber);

        if (orderDetailsList.isEmpty()) {
            throw new RuntimeException("Order not found");
        }

        VW_ORDER_DETAILS orderDetail = orderDetailsList.get(0);

        OrderResponseDto orderResponse = new OrderResponseDto();
        orderResponse.setOrderId(orderDetail.getOrderId());
        orderResponse.setUserId(orderDetail.getUserId());
        orderResponse.setTotalPrice(orderDetail.getTotalPrice());
        orderResponse.setTransactionDate(orderDetail.getTransactionDate());
        orderResponse.setOrderNumber(orderDetail.getOrderNumber());

        CustomerDto customerDTO = new CustomerDto();
        customerDTO.setName(orderDetail.getCustomerName());
        customerDTO.setAddress(orderDetail.getCustomerAddress());
        customerDTO.setPhone(orderDetail.getCustomerPhone());
        orderResponse.setCustomer(customerDTO);

        List<ProductDto> items = orderDetailsList.stream()
            .map(detail -> {
            	ProductDto item = new ProductDto();
                item.setId(detail.getId());
                item.setQuantity(detail.getProductQuantity());
                item.setPrice(detail.getTotalPrice());
                item.setProductId(detail.getProductId());
                item.setProductName(detail.getProductName());
                item.setColor(detail.getProductColor());
                item.setDescription(detail.getProductDescription());
                item.setActualPrice(detail.getTotalPrice());
                item.setProductQuantity(detail.getProductQuantity());
                item.setCreatorId(detail.getUserId()); 
                item.setCreationDate(detail.getTransactionDate()); 
                return item;
            })
            .collect(Collectors.toList());

        orderResponse.setItems(items);

        return orderResponse;
    }
}
