package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

	
	/*
	 * 
	 * CreateOrder Tests
	 * 
	*/
	
	@Test
	public void createValidOrderTest() {
		int orderId = 3;
		int userId = 6;
		int roleId = 4; // customer
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };
		double sum = 29.99 * 2 + 59.99 * 2;
		mockProduct(productIds);
		List<OrderDetails> odList = mockOrderDetails(userId, orderId, productIds, orderDetailIds);
		mockUser(userId, roleId);
		mockOrder(orderId, userId);

		// Act
		Order resultOrder = orderService.createOrder(userId, odList);

		// Assert		
		assertNotNull(resultOrder);
        assertEquals(userId, resultOrder.getUserId());
		assertTrue(resultOrder.getTotalPrice() == sum);
	}

	@Test
	public void createOrderWithHeadOfDepartment() {
		int orderId = 3;
		int userId = 6;
		int roleId = 1; 
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };

		List<OrderDetails> odList = mockOrderDetails(userId, orderId, productIds, orderDetailIds);

		// Mock user
		mockUser(userId, roleId);

		// Assert
		assertThrows(BusinessException.class, () -> {
			orderService.createOrder(userId, odList);
		});
	}

	@Test
	public void createOrderWithSuperAdmin() {
		int orderId = 3;
		int userId = 6;
		int roleId = 2; 
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };

		List<OrderDetails> odList = mockOrderDetails(userId, orderId, productIds, orderDetailIds);

		// Mock user
		mockUser(userId, roleId);

		// Assert
		assertThrows(BusinessException.class, () -> {
			orderService.createOrder(userId, odList);
		});
	}

	@Test
	public void createOrderWithAdmin() {
		int orderId = 3;
		int userId = 6;
		int roleId = 3; 
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };

		List<OrderDetails> odList = mockOrderDetails(userId, orderId, productIds, orderDetailIds);

		// Mock user
		mockUser(userId, roleId);

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
		List<OrderDetails> odList = mockOrderDetails(userId, orderId, productIds, orderDetailIds);
		
		// Mock invalid user
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
		int roleId = 4;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };
		
		// mocks
		mockOrder(orderId,userId);
		mockInvalidProducts(productIds);
		List<OrderDetails> odList = mockOrderDetails(userId, orderId, productIds, orderDetailIds);
		mockUser(userId,roleId);

		// Assert
		assertThrows(BusinessException.class, () -> {
			orderService.createOrder(userId, odList);
		});
	}

	@Test
	public void createOrderWithNegativeAndZeroProductQuantity() {
		int orderId = 3;
		int userId = 6;
		int roleId = 4;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };
		
		// Mocks
		mockProduct(productIds);
		mockOrder(orderId,userId);
		List<OrderDetails> odList = mockOrderDetailsWithInvalidQuantity(orderId, orderDetailIds, productIds);
		mockUser(userId, roleId);


		// Assert
		assertThrows(BusinessException.class, () -> {
			orderService.createOrder(userId, odList);
		});
	}

	@Test
	public void createOrderWithVeryLargeQuantityForProduct() {
		int orderId = 3;
		int userId = 6;
		int roleId = 4;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };

		// Mock products
		mockProduct(productIds);
		mockOrder(orderId, userId);
		mockUser(userId, roleId);
		
		List<OrderDetails> odList = mockOrderDetailsWithLargeQuantity(orderId, orderDetailIds, productIds);

		// Assert
		assertThrows(BusinessException.class, () -> {
			orderService.createOrder(userId, odList);
		});
	}

	@Test
	public void createOrderWithNoDetailsTest() {
		int orderId = 3;
		int userId = 6;
		int roleId = 4;
		// Mock order details
		mockUser(userId,roleId);
		mockOrder(orderId,userId);
		// empty order details list
		List<OrderDetails> odList = new ArrayList<OrderDetails>();
		// Act
		assertThrows(BusinessException.class, () -> {
			orderService.createOrder(userId, odList);
		});
	}

	/*
	 * 
	 * getOrderDetailByOrderNum Tests
	 * 
	*/
	
	
	@Test
	public void getOrderDetailByValidOrderNumTest() {
		int orderId = 3;
		int userId = 6;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };
		String orderNum = "ORD001";

		// Create two Vw_Order_Details instances
        List<Vw_Order_Details> orderList = Arrays.asList(
                createOrderDetail(orderDetailIds[0], orderId, 2, 59.98, productIds[0], userId, orderNum, "Dress", "Red", "High quality material"),
                createOrderDetail(orderDetailIds[1], orderId, 1, 59.99, productIds[1], userId, orderNum, "Blouse", "Blue", "Made of Silk")
            );
		Mockito.when(orderDetailsViewRepository.findByOrderNumber(orderNum)).thenReturn(orderList);
		// Act
		List<Vw_Order_Details> o = orderService.getOrderDetailsByOrderNum(orderNum);
		// Assert
		assertTrue(o != null);
		assertTrue(o.size()>0);
	}
	
	@Test
	public void getOrderDetailByInvalidOrderNumTest() {
		
		String orderNum = "ORD001";
		Mockito.when(orderDetailsViewRepository.findByOrderNumber(orderNum)).thenReturn(new ArrayList<Vw_Order_Details>());
		// Act
		assertThrows(BusinessException.class, () -> {
			orderService.getOrderDetailsByOrderNum(orderNum);
			});
	}

	@Test
	public void getOrderDetailByNullOrderNumTest() {
		
		String orderNum = null;
		Mockito.when(orderDetailsViewRepository.findByOrderNumber(orderNum)).thenReturn(new ArrayList<Vw_Order_Details>());
		// Act
		assertThrows(BusinessException.class, () -> {
			orderService.getOrderDetailsByOrderNum(orderNum);
			});
	}

	
	/*
	 * 
	 * getOrderDetailByUserId Tests
	 * 
	*/
	
	@Test
	public void getOrderDetailByUserIdWithMultipleOrdersTest() {

		int[] orderIds = {3,4};
		int userId = 6;
		int[] orderDetailIds = { 1, 2 };
		int[] productIds = { 1, 2 };
		String[] orderNums = {"ORD001","ORD002"};

		// Create 3 order details instances
		List<Vw_Order_Details> orderList = Arrays.asList(
	            createOrderDetail(orderDetailIds[0], orderIds[0], 2, 59.98, productIds[0], userId, orderNums[0], "Dress", "Red", "High quality material"),
	            createOrderDetail(orderDetailIds[1], orderIds[0], 1, 59.99, productIds[1], userId, orderNums[0], "Blouse", "Blue", "Made of Silk"),
	            createOrderDetail(orderDetailIds[0], orderIds[1], 1, 79.99, productIds[1], userId, orderNums[1], "Suit", "Black", "Made of premium material")
	        );
		Mockito.when(orderDetailsViewRepository.findByUserId(userId)).thenReturn(orderList);
		// Act
		List<Vw_Order_Details> orders = orderService.getOrderDetailsByUserId(userId);
		
		// Assert
		assertTrue(orders != null);	
		assertTrue(orders.size()>0);
	}

	@Test
	public void getOrderDetailByUserIdWithNoOrdersTest() {

		int userId = 6;
		Mockito.when(orderDetailsViewRepository.findByUserId(userId)).thenReturn(new ArrayList<Vw_Order_Details>());
		// Act
		assertThrows(BusinessException.class, () -> {
			orderService.getOrderDetailsByUserId(userId);
			});
	}
	
	@Test
	public void getOrderDetailByInvalidUserIdTest() {

		int userId = 6;
		Mockito.when(orderDetailsViewRepository.findByUserId(userId)).thenReturn(new ArrayList<Vw_Order_Details>());
		// Act	
		assertThrows(BusinessException.class, () -> {
			orderService.getOrderDetailsByUserId(userId);
			});
	}

	
		
	/*
	 * 
	 * Helper functions
	 * 
	 */
	
	private void mockUser(int userId, int roleId) {
		// Mock user
		User user = new User(userId, "Nada", "Elwazane", roleId, "passw0rd", "nada@mail.com", "622 Horreya Street",
						"01111111111", "Egyptian", "Female", new Date(), new Date(), 0);
		Mockito.when(userService.getUserById(userId)).thenReturn(Optional.of(user));

	}
	
	private void mockOrder(int orderId, int userId) {
		// Mock order repository
		Order invalidOrder = new Order(orderId, userId, new Date());
		Mockito.when(orderRepository.save(Mockito.any())).thenReturn(invalidOrder);

	}
	
	private List<OrderDetails> mockOrderDetails(int userId, int orderId, int[] productIds, int[] orderDetailIds) {
		// Mock order details
		OrderDetails od1 = new OrderDetails(orderDetailIds[0], orderId, productIds[0], 2);
		OrderDetails od2 = new OrderDetails(orderDetailIds[1], orderId, productIds[1], 2);
		Mockito.when(orderDetailsRepository.findById(orderDetailIds[0])).thenReturn(Optional.of(od1));
		Mockito.when(orderDetailsRepository.findById(orderDetailIds[1])).thenReturn(Optional.of(od2));

		List<OrderDetails> odList = Arrays.asList(od1, od2);
		return odList;
	}
	
	private void mockProduct(int[] productIds) {
		// Mock products
		Product p1 = new Product(productIds[0], "Blouse", 29.99, "Red", 50, "Premium material", new Date());
		Product p2 = new Product(productIds[1], "Dress", 59.99, "Blue", 75, "Premium material", new Date());

		Mockito.when(productService.findbyId(p1.getId())).thenReturn(p1);
		Mockito.when(productService.findbyId(p2.getId())).thenReturn(p2);

	}
	private void mockInvalidProducts(int[] productIds) {
		// Mock products
		Product p2 = new Product(productIds[1], "Dress", 59.99, "Blue", 75, "Premium material", new Date());

		Mockito.when(productRepository.findById(productIds[0])).thenReturn(Optional.empty());
		Mockito.when(productRepository.findById(p2.getId())).thenReturn(Optional.of(p2));
	}
	

	private List<OrderDetails> mockOrderDetailsWithInvalidQuantity(int orderId, int[] orderDetailIds,
			int[] productIds) {
		// Mock order details
		OrderDetails od1 = new OrderDetails(orderDetailIds[0], orderId, productIds[0], 0);
		OrderDetails od2 = new OrderDetails(orderDetailIds[1], orderId, productIds[1], -1);
		List<OrderDetails> odList = Arrays.asList(od1, od2);
		Mockito.when(orderDetailsRepository.findById(orderDetailIds[0])).thenReturn(Optional.of(od1));
		Mockito.when(orderDetailsRepository.findById(orderDetailIds[1])).thenReturn(Optional.of(od2));
		return odList;
	}
	
	private List<OrderDetails> mockOrderDetailsWithLargeQuantity(int orderId, int[] orderDetailIds, int[] productIds) {
		// Mock order details
		OrderDetails od1 = new OrderDetails(orderDetailIds[0], orderId, productIds[0], 100);
		OrderDetails od2 = new OrderDetails(orderDetailIds[1], orderId, productIds[1], 50);
		List<OrderDetails> odList = Arrays.asList(od1, od2);
		Mockito.when(orderDetailsRepository.findById(orderDetailIds[0])).thenReturn(Optional.of(od1));
		Mockito.when(orderDetailsRepository.findById(orderDetailIds[1])).thenReturn(Optional.of(od2));
		return odList;
	}
    private Vw_Order_Details createOrderDetail(int orderDetailId, int orderId, int productQuantity, double totalPrice, 
            int productId, int userId, String orderNum, String productName, 
            String productColor, String productDescription) {
    		
    	return new Vw_Order_Details(orderDetailId, orderId, productQuantity, totalPrice, productId, userId,
    					new Date(), productName, productColor, productDescription, "Nada Taher", 
    	                "622 Horreya Street", "01111111111", orderNum, 20);

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
