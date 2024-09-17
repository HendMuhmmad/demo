package com.example.demo.service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.CustomerDto;
import com.example.demo.model.dto.OrderResponseDto;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.orm.Vw_Order_Details;
import com.example.demo.repository.VWOrderDetailsRepository;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.model.dto.order.OrderDTO;
import com.example.demo.model.orm.Order;
import com.example.demo.model.orm.OrderDetails;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private VWOrderDetailsRepository orderDetailsViewRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderDetailsService orderDetailsService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;
	
	@Override
    public OrderResponseDto getOrderDetails(String orderNumber) {
        List<Vw_Order_Details> orderDetailsList = orderDetailsViewRepository.findByOrderNumber(orderNumber);

        if (orderDetailsList.isEmpty()) {
            throw new RuntimeException("Order not found");
        }

        Vw_Order_Details orderDetail = orderDetailsList.get(0);

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
	
	@Override
	public OrderDTO createOrder(int userId, List<OrderDetails> orderDetails) {
		// get user by Id
		Optional<User> result = userRepository.findById(userId);
		User theUser = null;
		if (result.isPresent()) {
			theUser = result.get();
		} else {
			throw new RuntimeException("Did not find user id - " + userId);
		}

		// check role
		// if not customer return exception
		if (theUser.getRoleId() != 4) {
			throw new RuntimeException("User is not a customer - " + userId);
		}

		// create an order
		Order order = new Order();
		order.setUserId(userId);
		order.setOrderNumber(generateUUID()); 
		order.setTransactionDate(new Date());
		orderRepository.save(order);
		// create orderDetails
		for (OrderDetails orderDetail : orderDetails) {
			// validate order detail
			Product product = validateOrderDetailAndReturnProduct(orderDetail);
			int remainingQuantity = product.getStockQuantity() - orderDetail.getQuantity();
			// update quantity
			product.setStockQuantity(remainingQuantity);
			// need repository to update
			productRepository.save(product);
			orderDetail.setOrderId(order.getId());
			orderDetailsService.createOrderDetail(orderDetail);
			double orderDetailPrice = product.getPrice() * orderDetail.getQuantity();
			order.setTotalPrice(order.getTotalPrice() + orderDetailPrice);
		}

		return OrderMapper.INSTANCE.mapOrder(order);
	}

	
	


	private Product validateOrderDetailAndReturnProduct(OrderDetails orderDetail) {
		int productId = orderDetail.getProduct_id();
		Optional<Product> result = productRepository.findById(productId);
		Product theProduct = null;
		if (result.isPresent()) {
			theProduct = result.get();
		} else {
			throw new RuntimeException("Did not find product id - " + productId);
		}
		int quantityRequested = orderDetail.getQuantity();
		// validate quantity
		int stockQuantity = theProduct.getStockQuantity();
		if(quantityRequested <= 0 ) {
			throw new RuntimeException("Quantity must be positive");
		}
		if (stockQuantity < quantityRequested) {
			throw new RuntimeException("Not enough stock available. Requested " + quantityRequested + " and only "
					+ stockQuantity + " available");

		}
		return theProduct;
	}
	
    private String generateUUID() {
        // Generate a UUID
        UUID uuid = UUID.randomUUID();
        
        // Convert the UUID to a string
        String uuidAsString = uuid.toString();
        
        // Return the UUID string
        return uuidAsString;
    }


}
