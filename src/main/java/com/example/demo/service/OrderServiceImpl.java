package com.example.demo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.model.dto.CustomerDto;
import com.example.demo.model.dto.OrderResponseDto;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.dto.order.OrderDTO;
import com.example.demo.model.orm.Order;
import com.example.demo.model.orm.OrderDetails;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.Vw_Order_Details;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.VWOrderDetailsRepository;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private VWOrderDetailsRepository orderDetailsViewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailsService orderDetailsService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    public ResponseEntity<OrderResponseDto> getOrderDetailsByOrderNum(String orderNumber) throws BusinessException {
	List<Vw_Order_Details> orderDetailsList = orderDetailsViewRepository.findByOrderNumber(orderNumber);
	if (orderDetailsList.isEmpty()) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(constructOrderResponseDto(orderDetailsList));
    }

    @Override
    public ResponseEntity<List<OrderResponseDto>> getOrderDetailsByUserId(int userId) throws BusinessException {
	List<Vw_Order_Details> orderDetailsList = orderDetailsViewRepository.findByUserId(userId);
	// get order Ids
	List<Integer> orderIds = getOrderIds(orderDetailsList);
	List<OrderResponseDto> orderResponseDtos = new ArrayList<OrderResponseDto>();
	for (Integer orderId : orderIds) {
	    // get array of Vw_Order_Details for each orderId
	    List<Vw_Order_Details> orderDetails = getOrdersForOrderId(orderDetailsList, orderId);
	    orderResponseDtos.add(constructOrderResponseDto(orderDetails));
	}
	return ResponseEntity.ok(orderResponseDtos);
    }

    private List<Integer> getOrderIds(List<Vw_Order_Details> orderDetailsList) {
	List<Integer> orderIds = new ArrayList<Integer>();
	HashSet<Integer> orderIdsSet = new HashSet<Integer>();
	for (Vw_Order_Details Vw_order_detail : orderDetailsList) {
	    orderIdsSet.add(Vw_order_detail.getOrderId());
	}
	orderIds.addAll(orderIdsSet);
	return orderIds;
    }

    private List<Vw_Order_Details> getOrdersForOrderId(List<Vw_Order_Details> orderDetailsList, int orderId) {
	List<Vw_Order_Details> orderDetails = new ArrayList<Vw_Order_Details>();
	for (Vw_Order_Details Vw_order_detail : orderDetailsList) {
	    if (Vw_order_detail.getOrderId() == orderId) {
		orderDetails.add(Vw_order_detail);
	    }
	}

	return orderDetails;
    }

    public OrderResponseDto constructOrderResponseDto(List<Vw_Order_Details> details) {
	Vw_Order_Details orderDetail = details.get(0);

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

	List<ProductDto> items = details.stream()
		.map(detail -> {
		    ProductDto item = new ProductDto();
		    item.setStockQuantity(detail.getProductQuantity());
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

    @Override
    public OrderDTO createOrder(int userId, List<OrderDetails> orderDetails) throws BusinessException {
	// get user by Id
	User user = userService.getUserById(userId).get();
	if (user == null)
	    throw new BusinessException(" user not found ");

	// check role
	// if not customer return exception
	if (user.getRoleId() != 4) {
	    throw new BusinessException("User is not a customer", new Object[] { "userId" });
	}

	// create an order
	Order order = new Order();
	order.setUserId(userId);
	order.setOrderNumber(generateUUID());
	order.setTransactionDate(new Date());
	orderRepository.save(order);
	// create orderDetails
	for (OrderDetails orderDetail : orderDetails) {
	    Product returnProduct = null;

	    if (getProductById(orderDetail.getProduct_id()).equals(true))
		returnProduct = getProductById(orderDetail.getProduct_id());

	    validateOrder(orderDetail, returnProduct);
	    int remainingQuantity = returnProduct.getStockQuantity() - orderDetail.getQuantity();
	    returnProduct.setStockQuantity(remainingQuantity);
	    productService.save(returnProduct, userId);
	    orderDetail.setOrderId(order.getId());
	    orderDetailsService.createOrderDetail(orderDetail);
	    double orderDetailPrice = returnProduct.getPrice() * orderDetail.getQuantity();
	    order.setTotalPrice(order.getTotalPrice() + orderDetailPrice);
	}

	return OrderMapper.INSTANCE.mapOrder(order);
    }

    public void validateOrder(OrderDetails orderDetails, Product product) {

	if (orderDetails.getProduct_id() == null || orderDetails.getProduct_id() == 0)
	    throw new BusinessException("you must enter product ");

	if (orderDetails.getQuantity() == null)
	    throw new BusinessException("you must enter quentity ");

	if (orderDetails.getQuantity() < 0)
	    throw new BusinessException("quentity must be gretter than zero ");

	if (product.getId() == null)
	    throw new BusinessException(" product does not exist ");

	if (product.getStockQuantity() == null || product.getStockQuantity() > 0)
	    throw new BusinessException(" out of  Stock ");

    }

    private String generateUUID() {
	// Generate a UUID
	UUID uuid = UUID.randomUUID();

	// Convert the UUID to a string
	String uuidAsString = uuid.toString();

	// Return the UUID string
	return uuidAsString;
    }

    private Product getProductById(Integer id) {
	return productService.findbyId(id);
    }
}
