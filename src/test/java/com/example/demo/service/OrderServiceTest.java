package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.dto.OrderResponseDto;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.dto.order.OrderDTO;
import com.example.demo.model.orm.Order;
import com.example.demo.model.orm.OrderDetails;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.Vw_Order_Details;
import com.example.demo.repository.OrderDetailsRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VWOrderDetailsRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

	@InjectMocks
	OrderServiceImpl orderService;

	@MockBean
	UserRepository userRepository;

	@MockBean
	OrderRepository orderRepository;

	@MockBean
	ProductRepository productRepository;

	@MockBean
	OrderDetailsRepository orderDetailsRepository;

	@MockBean
	private VWOrderDetailsRepository orderDetailsViewRepository;

	@MockBean
	private OrderDetailsService orderDetailsService;

	@MockBean
	private ProductService productService;

	@MockBean
	public UserService userService;

	@Test
	public void createValidOrderTest() {
		int orderId = 3;
		int userId = 6;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };
		double sum = 29.99 * 2 + 59.99 * 2;

		// Mock products
		Product p1 = new Product(productIds[0], "Blouse", 29.99, "Red", 50, "Premium material", new Date());
		Product p2 = new Product(productIds[1], "Dress", 59.99, "Blue", 75, "Premium material", new Date());

		Mockito.when(productRepository.findById(p1.getId())).thenReturn(Optional.of(p1));
		Mockito.when(productRepository.findById(p2.getId())).thenReturn(Optional.of(p2));

		// Mock order details
		Order invalidOrder = new Order(orderId, userId, new Date(), "ORD001", 0);
		OrderDetails od1 = new OrderDetails(orderDetailIds[0], orderId, p1.getId(), 2);
		OrderDetails od2 = new OrderDetails(orderDetailIds[1], orderId, p2.getId(), 2);

		List<OrderDetails> odList = Arrays.asList(od1, od2);

		// Mock user
		User user = new User(userId, "Nada", "Elwazane", 4, "passw0rd", "nada@mail.com", "622 Horreya Street",
				"01111111111", "Egyptian", "Female", new Date(), new Date(), 0);
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// Mock order repository
		Mockito.when(orderRepository.save(Mockito.any())).thenReturn(invalidOrder);

		// Mock order details repository
		Mockito.when(orderDetailsRepository.findById(orderDetailIds[0])).thenReturn(Optional.of(od1));
		Mockito.when(orderDetailsRepository.findById(orderDetailIds[1])).thenReturn(Optional.of(od2));

		// Act
		OrderDTO o = orderService.createOrder(userId, odList);

		// Assert
		assertTrue(o.getTotalPrice() == sum);
	}

	@Test
	public void createOrderWithHeadOfDepartment() {
		int orderId = 3;
		int userId = 6;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };

		// Mock order details
		OrderDetails od1 = new OrderDetails(orderDetailIds[0], orderId, productIds[0], 2);
		OrderDetails od2 = new OrderDetails(orderDetailIds[1], orderId, productIds[1], 2);

		List<OrderDetails> odList = Arrays.asList(od1, od2);

		// Mock user
		User user = new User(userId, "Nada", "Elwazane", 1, "passw0rd", "nada@mail.com", "622 Horreya Street",
				"01111111111", "Egyptian", "Female", new Date(), new Date(), 0);
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// Assert
		assertThrows(BusinessException.class, () -> {
			orderService.createOrder(userId, odList);
		});
	}

	@Test
	public void createOrderWithSuperAdmin() {
		int orderId = 3;
		int userId = 6;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };

		// Mock order details
		OrderDetails od1 = new OrderDetails(orderDetailIds[0], orderId, productIds[0], 2);
		OrderDetails od2 = new OrderDetails(orderDetailIds[1], orderId, productIds[1], 2);

		List<OrderDetails> odList = Arrays.asList(od1, od2);

		// Mock user
		User user = new User(userId, "Nada", "Elwazane", 2, "passw0rd", "nada@mail.com", "622 Horreya Street",
				"01111111111", "Egyptian", "Female", new Date(), new Date(), 0);
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// Assert
		assertThrows(BusinessException.class, () -> {
			orderService.createOrder(userId, odList);
		});
	}

	@Test
	public void createOrderWithAdmin() {
		int orderId = 3;
		int userId = 6;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };

		// Mock order details
		OrderDetails od1 = new OrderDetails(orderDetailIds[0], orderId, productIds[0], 2);
		OrderDetails od2 = new OrderDetails(orderDetailIds[1], orderId, productIds[1], 2);

		List<OrderDetails> odList = Arrays.asList(od1, od2);

		// Mock user
		User user = new User(userId, "Nada", "Elwazane", 3, "passw0rd", "nada@mail.com", "622 Horreya Street",
				"01111111111", "Egyptian", "Female", new Date(), new Date(), 0);
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// Assert
		assertThrows(BusinessException.class, () -> {
			orderService.createOrder(userId, odList);
		});
	}

	@Test
	public void createOrderWithInvalidUserId() {
		int orderId = 3;
		int userId = 6;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };

		// Mock order details
		OrderDetails od1 = new OrderDetails(orderDetailIds[0], orderId, productIds[0], 2);
		OrderDetails od2 = new OrderDetails(orderDetailIds[1], orderId, productIds[1], 2);

		List<OrderDetails> odList = Arrays.asList(od1, od2);

		// Mock user
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// Assert
		assertThrows(BusinessException.class, () -> {
			orderService.createOrder(userId, odList);
		});
	}

	@Test
	public void createOrderWithInvalidProductId() {
		int orderId = 3;
		int userId = 6;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };

		// Mock products
		Product p2 = new Product(productIds[1], "Dress", 59.99, "Blue", 75, "Premium material", new Date());

		Mockito.when(productRepository.findById(productIds[0])).thenReturn(Optional.empty());
		Mockito.when(productRepository.findById(p2.getId())).thenReturn(Optional.of(p2));

		// Mock order details
		Order invalidOrder = new Order(orderId, userId, new Date(), "ORD001", 0);
		OrderDetails od1 = new OrderDetails(orderDetailIds[0], orderId, productIds[0], 2);
		OrderDetails od2 = new OrderDetails(orderDetailIds[1], orderId, p2.getId(), 2);

		List<OrderDetails> odList = Arrays.asList(od1, od2);

		// Mock user
		User user = new User(userId, "Nada", "Elwazane", 4, "passw0rd", "nada@mail.com", "622 Horreya Street",
				"01111111111", "Egyptian", "Female", new Date(), new Date(), 0);
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// Mock order repository
		Mockito.when(orderRepository.save(Mockito.any())).thenReturn(invalidOrder);

		// Mock order details repository
		Mockito.when(orderDetailsRepository.findById(orderDetailIds[0])).thenReturn(Optional.of(od1));
		Mockito.when(orderDetailsRepository.findById(orderDetailIds[1])).thenReturn(Optional.of(od2));

		// Assert
		assertThrows(BusinessException.class, () -> {
			orderService.createOrder(userId, odList);
		});
	}

	@Test
	public void createOrderWithNegativeAndZeroProductQuantity() {
		int orderId = 3;
		int userId = 6;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };

		// Mock products
		Product p1 = new Product(productIds[0], "Blouse", 29.99, "Red", 50, "Premium material", new Date());
		Product p2 = new Product(productIds[1], "Dress", 59.99, "Blue", 75, "Premium material", new Date());

		Mockito.when(productRepository.findById(productIds[0])).thenReturn(Optional.of(p1));
		Mockito.when(productRepository.findById(p2.getId())).thenReturn(Optional.of(p2));

		// Mock order details
		Order invalidOrder = new Order(orderId, userId, new Date(), "ORD001", 0);
		OrderDetails od1 = new OrderDetails(orderDetailIds[0], orderId, p1.getId(), 0);
		OrderDetails od2 = new OrderDetails(orderDetailIds[1], orderId, p2.getId(), -1);

		List<OrderDetails> odList = Arrays.asList(od1, od2);

		// Mock user
		User user = new User(userId, "Nada", "Elwazane", 4, "passw0rd", "nada@mail.com", "622 Horreya Street",
				"01111111111", "Egyptian", "Female", new Date(), new Date(), 0);
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// Mock order repository
		Mockito.when(orderRepository.save(Mockito.any())).thenReturn(invalidOrder);

		// Mock order details repository
		Mockito.when(orderDetailsRepository.findById(orderDetailIds[0])).thenReturn(Optional.of(od1));
		Mockito.when(orderDetailsRepository.findById(orderDetailIds[1])).thenReturn(Optional.of(od2));

		// Assert
		assertThrows(BusinessException.class, () -> {
			orderService.createOrder(userId, odList);
		});
	}

	@Test
	public void createOrderWithVeryLargeQuantityForProduct() {
		int orderId = 3;
		int userId = 6;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };

		// Mock products
		Product p1 = new Product(productIds[0], "Blouse", 29.99, "Red", 50, "Premium material", new Date());
		Product p2 = new Product(productIds[1], "Dress", 59.99, "Blue", 75, "Premium material", new Date());

		Mockito.when(productRepository.findById(p1.getId())).thenReturn(Optional.of(p1));
		Mockito.when(productRepository.findById(p2.getId())).thenReturn(Optional.of(p2));

		// Mock order details
		Order invalidOrder = new Order(orderId, userId, new Date(), "ORD001", 0);
		OrderDetails od1 = new OrderDetails(orderDetailIds[0], orderId, p1.getId(), 100);
		OrderDetails od2 = new OrderDetails(orderDetailIds[1], orderId, p2.getId(), 50);

		List<OrderDetails> odList = Arrays.asList(od1, od2);

		// Mock user
		User user = new User(userId, "Nada", "Elwazane", 4, "passw0rd", "nada@mail.com", "622 Horreya Street",
				"01111111111", "Egyptian", "Female", new Date(), new Date(), 0);
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// Mock order repository
		Mockito.when(orderRepository.save(Mockito.any())).thenReturn(invalidOrder);

		// Mock order details repository
		Mockito.when(orderDetailsRepository.findById(orderDetailIds[0])).thenReturn(Optional.of(od1));
		Mockito.when(orderDetailsRepository.findById(orderDetailIds[1])).thenReturn(Optional.of(od2));

		// Assert
		assertThrows(BusinessException.class, () -> {
			orderService.createOrder(userId, odList);
		});
	}

	@Test
	public void getOrderDetailByValidOrderWithSingleDetailNumTest() {

		int orderId = 3;
		int userId = 6;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };
		String orderNum = "ORD001";

		// Create two Vw_Order_Details instances
		Vw_Order_Details order1 = new Vw_Order_Details(orderDetailIds[0], // id
				orderId, // orderId
				2, // productQuantity
				59.98, // totalPrice
				productIds[0], // productId
				userId, // userId
				new Date(), // transactionDate
				"Dress", // productName
				"Red", // productColor
				"High quality material", // productDescription
				"Nada Taher", // customerName
				"622 Horreya Street", // customerAddress
				"01111111111", // customerPhone
				orderNum, // orderNumber
				20 // stockQuantity
		);


		List<Vw_Order_Details> orderList = Arrays.asList(order1);

		Mockito.when(orderDetailsViewRepository.findByOrderNumber(orderNum)).thenReturn(orderList);
		// Act
		ResponseEntity<OrderResponseDto> o = orderService.getOrderDetailsByOrderNum(orderNum);
		// Assert
		assertTrue(o != null);
        assertTrue(o.getStatusCode() == HttpStatus.OK);
		validateOrderResponse(o.getBody(), orderList);

	}

	
	@Test
	public void getOrderDetailByValidOrderNumWithMultipleDetailsTest() {

		int orderId = 3;
		int userId = 6;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };
		String orderNum = "ORD001";

		// Create two Vw_Order_Details instances
		Vw_Order_Details order1 = new Vw_Order_Details(orderDetailIds[0], // id
				orderId, // orderId
				2, // productQuantity
				59.98, // totalPrice
				productIds[0], // productId
				userId, // userId
				new Date(), // transactionDate
				"Dress", // productName
				"Red", // productColor
				"High quality material", // productDescription
				"Nada Taher", // customerName
				"622 Horreya Street", // customerAddress
				"01111111111", // customerPhone
				orderNum, // orderNumber
				20 // stockQuantity
		);

		Vw_Order_Details order2 = new Vw_Order_Details(orderDetailIds[1], // id
				orderId, // orderId
				1, // productQuantity
				59.99, // totalPrice
				productIds[1], // productId
				userId, // userId
				new Date(), // transactionDate
				"Blouse", // productName
				"Blue", // productColor
				"Made of Silk", // productDescription
				"Nada Taher", // customerName
				"622 Horreya Street", // customerAddress
				"01111111111", // customerPhone
				orderNum, // orderNumber
				20 // stockQuantity
		);

		List<Vw_Order_Details> orderList = Arrays.asList(order1, order2);

		Mockito.when(orderDetailsViewRepository.findByOrderNumber(orderNum)).thenReturn(orderList);
		// Act
		ResponseEntity<OrderResponseDto> o = orderService.getOrderDetailsByOrderNum(orderNum);
		// Assert
		assertTrue(o != null);
        assertTrue(o.getStatusCode() == HttpStatus.OK);
		validateOrderResponse(o.getBody(), orderList);

	}

	
	@Test
	public void getOrderDetailByInvalidOrderNumTest() {
		String orderNum = "ORD001";
		Mockito.when(orderDetailsViewRepository.findByOrderNumber(orderNum)).thenReturn(new ArrayList<Vw_Order_Details>());
		// Act
		ResponseEntity<OrderResponseDto> o = orderService.getOrderDetailsByOrderNum(orderNum);
		assertTrue(o != null);
        assertTrue(o.getStatusCode() == HttpStatus.NOT_FOUND);

	}

	@Test
	public void getOrderDetailByUserIdWithMultipleOrdersTest() {

		int[] orderIds = {3,4};
		int userId = 6;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };
		String[] orderNums = {"ORD001","ORD002"};

		// Create two Vw_Order_Details instances
		Vw_Order_Details orderDetail1Order1 = new Vw_Order_Details(orderDetailIds[0], // id
				orderIds[0], // orderId
				2, // productQuantity
				59.98, // totalPrice
				productIds[0], // productId
				userId, // userId
				new Date(), // transactionDate
				"Dress", // productName
				"Red", // productColor
				"High quality material", // productDescription
				"Nada Taher", // customerName
				"622 Horreya Street", // customerAddress
				"01111111111", // customerPhone
				orderNums[0], // orderNumber
				20 // stockQuantity
		);

		Vw_Order_Details orderDetail2Order1 = new Vw_Order_Details(orderDetailIds[1], // id
				orderIds[0], // orderId
				1, // productQuantity
				59.99, // totalPrice
				productIds[1], // productId
				userId, // userId
				new Date(), // transactionDate
				"Blouse", // productName
				"Blue", // productColor
				"Made of Silk", // productDescription
				"Nada Taher", // customerName
				"622 Horreya Street", // customerAddress
				"01111111111", // customerPhone
				orderNums[0], // orderNumber
				20 // stockQuantity
		);
		
		Vw_Order_Details orderDetail1Order2 = new Vw_Order_Details(orderDetailIds[1], // id
				orderIds[1], // orderId
				1, // productQuantity
				79.99, // totalPrice
				productIds[1], // productId
				userId, // userId
				new Date(), // transactionDate
				"Suit", // productName
				"Black", // productColor
				"Made of premium material", // productDescription
				"Nada Taher", // customerName
				"622 Horreya Street", // customerAddress
				"01111111111", // customerPhone
				orderNums[1], // orderNumber
				30 // stockQuantity
		);

		List<Vw_Order_Details> orderList = Arrays.asList(orderDetail1Order1, orderDetail2Order1,orderDetail1Order2);

		Mockito.when(orderDetailsViewRepository.findByUserId(userId)).thenReturn(orderList);
		// Act
		ResponseEntity<List<OrderResponseDto>> orders = orderService.getOrderDetailsByUserId(userId);
		// Assert
		assertTrue(orders != null);
        assertTrue(orders.getStatusCode() == HttpStatus.OK);
        for (OrderResponseDto order:orders.getBody()) {
        	List<Vw_Order_Details> applicableOrderList =getOrdersForOrderId(orderList,order.getOrderId());
        	validateOrderResponse(order, applicableOrderList);
        }
		
	}
	
	@Test
	public void getOrderDetailByUserIdWithSingleOrderTest() {

		int orderId = 3;
		int userId = 6;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };
		String orderNum = "ORD001";

		// Create two Vw_Order_Details instances
		Vw_Order_Details order1 = new Vw_Order_Details(orderDetailIds[0], // id
				orderId, // orderId
				2, // productQuantity
				59.98, // totalPrice
				productIds[0], // productId
				userId, // userId
				new Date(), // transactionDate
				"Dress", // productName
				"Red", // productColor
				"High quality material", // productDescription
				"Nada Taher", // customerName
				"622 Horreya Street", // customerAddress
				"01111111111", // customerPhone
				orderNum, // orderNumber
				20 // stockQuantity
		);

		Vw_Order_Details order2 = new Vw_Order_Details(orderDetailIds[1], // id
				orderId, // orderId
				1, // productQuantity
				59.99, // totalPrice
				productIds[1], // productId
				userId, // userId
				new Date(), // transactionDate
				"Blouse", // productName
				"Blue", // productColor
				"Made of Silk", // productDescription
				"Nada Taher", // customerName
				"622 Horreya Street", // customerAddress
				"01111111111", // customerPhone
				orderNum, // orderNumber
				20 // stockQuantity
		);

		List<Vw_Order_Details> orderList = Arrays.asList(order1, order2);

		Mockito.when(orderDetailsViewRepository.findByUserId(userId)).thenReturn(orderList);
		// Act
		ResponseEntity<List<OrderResponseDto>> orders = orderService.getOrderDetailsByUserId(userId);
		// Assert
		assertTrue(orders != null);
        assertTrue(orders.getStatusCode() == HttpStatus.OK);
        for (OrderResponseDto order:orders.getBody()) {
        	List<Vw_Order_Details> applicableOrderList =getOrdersForOrderId(orderList,order.getOrderId());
        	validateOrderResponse(order, applicableOrderList);
        }
		
	}
	
	@Test
	public void getOrderDetailByUserIdWithNoOrdersTest() {

		int userId = 6;
		Mockito.when(orderDetailsViewRepository.findByUserId(userId)).thenReturn(new ArrayList<Vw_Order_Details>());
		// Act
		ResponseEntity<List<OrderResponseDto>> orders = orderService.getOrderDetailsByUserId(userId);
		// Assert
		assertTrue(orders != null);
        assertTrue(orders.getStatusCode() == HttpStatus.OK);
        assertTrue(orders.getBody().size()==0);
		
	}
	@Test
	public void getOrderDetailByInvalidUserIdTest() {

		int userId = 6;
		Mockito.when(orderDetailsViewRepository.findByUserId(userId)).thenReturn(new ArrayList<Vw_Order_Details>());
		// Act
		ResponseEntity<List<OrderResponseDto>> orders = orderService.getOrderDetailsByUserId(userId);
		// Assert
		assertTrue(orders != null);
        assertTrue(orders.getStatusCode() == HttpStatus.OK);
        assertTrue(orders.getBody().size()==0);
		
	}
	
	
	private static List<Vw_Order_Details> getOrdersForOrderId(List<Vw_Order_Details> orderDetailsList, int orderId) {
		List<Vw_Order_Details> orderDetails = new ArrayList<Vw_Order_Details>();
		for (Vw_Order_Details Vw_order_detail : orderDetailsList) {
			if (Vw_order_detail.getOrderId() == orderId) {
				orderDetails.add(Vw_order_detail);
			}
		}

		return orderDetails;
	}

	private static void validateOrderResponse(OrderResponseDto orderResponse, List<Vw_Order_Details> orderDetails) {
		double sum = calculateTotalPrice(orderDetails);
		assertTrue(orderResponse != null, "OrderResponseDto should not be null");

        // Validate basic order properties
        assertTrue(orderDetails.get(0).getOrderId() == orderResponse.getOrderId());
        assertTrue(orderDetails.get(0).getUserId() == orderResponse.getUserId());
        assertTrue(orderDetails.get(0).getTransactionDate().equals(orderResponse.getTransactionDate()));
        assertTrue(orderDetails.get(0).getOrderNumber().equals(orderResponse.getOrderNumber()));

        // Validate customer properties
        assertTrue(orderResponse.getCustomer() != null);
        assertTrue(orderDetails.get(0).getCustomerName().equals(orderResponse.getCustomer().getName()));
        assertTrue(orderDetails.get(0).getCustomerAddress().equals(orderResponse.getCustomer().getAddress()));
        assertTrue(orderDetails.get(0).getCustomerPhone().equals(orderResponse.getCustomer().getPhone()));

        // Validate items
        List<ProductDto> productDtos = orderDetails.stream().map(detail -> {
            ProductDto productDto = new ProductDto();
            productDto.setStockQuantity(detail.getStockQuantity());
            productDto.setPrice(detail.getTotalPrice());
            productDto.setProductName(detail.getProductName());
            productDto.setColor(detail.getProductColor());
            productDto.setDescription(detail.getProductDescription());
            productDto.setProductQuantity(detail.getProductQuantity());
            return productDto;
        }).collect(Collectors.toList());

        assertTrue(productDtos.size() == orderResponse.getItems().size());
        for (int i = 0; i < productDtos.size(); i++) {
            assertTrue(productDtos.get(i).getProductName().equals(orderResponse.getItems().get(i).getProductName()));
            assertTrue(productDtos.get(i).getColor().equals(orderResponse.getItems().get(i).getColor()));
            assertTrue(productDtos.get(i).getDescription().equals(orderResponse.getItems().get(i).getDescription()));
            assertTrue(productDtos.get(i).getProductQuantity() == orderResponse.getItems().get(i).getProductQuantity());
            assertTrue(productDtos.get(i).getPrice() == orderResponse.getItems().get(i).getPrice());
            assertTrue(productDtos.get(i).getStockQuantity() == orderResponse.getItems().get(i).getStockQuantity());
        }
		assertTrue(orderResponse.getTotalPrice() == sum);

	}

	private static double calculateTotalPrice(List<Vw_Order_Details> orderDetails) {
		double totalPrice = 0.0;

		for (Vw_Order_Details orderDetail : orderDetails) {
			totalPrice += orderDetail.getTotalPrice();
		}

		return totalPrice;
	}

}
