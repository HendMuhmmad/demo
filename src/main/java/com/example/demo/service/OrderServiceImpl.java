package com.example.demo.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.workflow.WFStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Order;
import com.example.demo.model.orm.OrderDetails;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.OrderDetailsData;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderDetailsDataRepository;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDetailsDataRepository orderDetailsViewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailsService orderDetailsService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    public List<OrderDetailsData> getOrderDetailsByOrderNum(String orderNumber) {
	if (orderNumber == null) {
	    throw new BusinessException("Please include an order number.");
	}

	List<OrderDetailsData> orderDetailsList = orderDetailsViewRepository.findByOrderNumber(orderNumber);

	if (orderDetailsList.isEmpty()) {
	    throw new BusinessException("Order Number not found");
	}
	return orderDetailsList;
    }

    @Override
    public List<OrderDetailsData> getOrderDetailsByUserId(Long userId) throws BusinessException {
	if (userId == null) {
	    throw new BusinessException("Please include a userId");
	}
	List<OrderDetailsData> orderDetailsList = orderDetailsViewRepository.findByUserId(userId);

	if (orderDetailsList.isEmpty()) {
	    throw new BusinessException("No orders found");
	}
	return orderDetailsList;
    }

    @Override
    public Order createOrder(Long userId, List<OrderDetails> orderDetails) throws BusinessException {

	validateRole(userService.getUserById(userId).get());
	
	
	// create an order
	Order order = createAndSaveOrder(userId);
	
	// validate presence of orderDetails
	validateOrderDetailsList(orderDetails);
	// create orderDetails
	for (OrderDetails orderDetail : orderDetails) {
	    Product returnProduct = null;
	    returnProduct = getProductById(orderDetail.getProduct_id());
	    // Validation
	    validateOrder(orderDetail, returnProduct, userId);
	    // update orderDetail
	    orderDetail.setOrderId(order.getId());
	    orderDetailsService.createOrderDetail(orderDetail);
	    // calculate total price
	    double orderDetailPrice = returnProduct.getPrice() * orderDetail.getQuantity();
	    order.setTotalPrice(order.getTotalPrice() + orderDetailPrice);
	}

	return order;
    }

    private void validateOrderDetailsList(List<OrderDetails> orderDetails) {
		if (orderDetails.isEmpty()) {
			throw new BusinessException("No products included in order");
		}
		
	}

	private void validateOrderQuantityAndUpdateProduct(Product returnProduct, OrderDetails orderDetail, Long userId) {
	int remainingQuantity = returnProduct.getStockQuantity() - orderDetail.getQuantity();
	if (remainingQuantity < 0) {
	    throw new BusinessException("Out of stock");
	}
	// returnProduct.setStockQuantity(remainingQuantity);
	productService.updateProductQuantityWithOutAuth(returnProduct.getId(),remainingQuantity);
    }

    private Order createAndSaveOrder(Long userId) {
	Order order = new Order(userId, new Date(), generateUUID());
	orderRepository.save(order);
	return order;
    }

    public void validateOrder(OrderDetails orderDetail, Product product, Long userId) {
	validateOrderDetailes(orderDetail);
	validateProduct(product);
	validateOrderQuantityAndUpdateProduct(product, orderDetail, userId);
    }

    public void validateOrderDetailes(OrderDetails orderDetails) {

	if (orderDetails == null)
	    throw new BusinessException("Order details cannot be null or empty");
	// check that there are products in the order

	if (orderDetails.getProduct_id() == null || orderDetails.getProduct_id() == 0)
	    throw new BusinessException("product does not exist");

	if (orderDetails.getQuantity() == null)
	    throw new BusinessException("you must enter quantity");

	if (orderDetails.getQuantity() < 0 || orderDetails.getQuantity() == 0)
	    throw new BusinessException("quantity must be greater than zero");

    }

    public void validateProduct(Product product) {
	if (product == null || product.getId() == null)
	    throw new BusinessException("product does not exist");

	if (product.getStockQuantity() == null || product.getStockQuantity() < 0)
	    throw new BusinessException("out of  Stock");
    }

    private void validateRole(User user) {
	if (user == null) {
	    throw new BusinessException(" user not found ");
	}
	if (user.getRoleId() != 4)
	    throw new BusinessException("User is not a customer");
    }

    private String generateUUID() {
	// Generate a UUID
	UUID uuid = UUID.randomUUID();

	// Convert the UUID to a string
	String uuidAsString = uuid.toString();

	// Return the UUID string
	return uuidAsString;
    }

    private Product getProductById(Long productId) {
	if (productId == null) {
	    throw new BusinessException("product does not exist");
	}
	return productService.findbyId(productId);
    }
}
