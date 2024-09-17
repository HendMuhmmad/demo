package com.example.demo.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.CustomerDto;
import com.example.demo.model.dto.OrderResponseDto;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.orm.Vw_Order_Details;
import com.example.demo.repository.VWOrderDetailsRepository;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private VWOrderDetailsRepository orderDetailsViewRepository;

    public OrderResponseDto getOrderDetails(String orderNumber) {
        List<Vw_Order_Details> orderDetailsList = orderDetailsViewRepository.findByOrderNumber(orderNumber);

        if (orderDetailsList.isEmpty()) {
            throw new RuntimeException("Order not found");
        }

        Vw_Order_Details orderDetail = orderDetailsList.get(0);

        OrderResponseDto orderResponse = new OrderResponseDto();
        orderResponse.setOrderId(orderDetail.getOrderId());
        orderResponse.setUserId(orderDetail.getUserId());
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
                item.setQuantity(detail.getProductQuantity());
                item.setPrice(detail.getTotalPrice());
                item.setProductName(detail.getProductName());
                item.setColor(detail.getProductColor());
                item.setDescription(detail.getProductDescription());
                item.setProductQuantity(detail.getProductQuantity());
                item.setCreatorId(detail.getUserId()); 
                item.setCreationDate(detail.getTransactionDate()); 
                return item;
            })
            .collect(Collectors.toList());

        orderResponse.setItems(items);
        double totalPrice = items.stream()
                .mapToDouble(ProductDto::getPrice)
                .sum();
        orderResponse.setTotalPrice(totalPrice);
        return orderResponse;
    }
}
