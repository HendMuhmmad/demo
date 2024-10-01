package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.enums.RoleEnum;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.dto.ProductUpdateStockQuantityDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void saveProduct() throws Exception {
	ProductDto product = new ProductDto(20, 100.0, "Product 1", "Red", "Description 1", 20,0L, null, RoleEnum.ADMIN.getCode());
	mockMvc.perform(post("/api/product/createProduct")
		.contentType("application/json")
		.content(objectMapper.writeValueAsString(product)))
		.andExpect(status().isOk());
    }

    @Test
    public void saveProduct_withoutProductName() throws Exception {
	ProductDto product = new ProductDto(20, 100.0, null, "Red", "Description 1", 20, 0L, null, RoleEnum.ADMIN.getCode());
	mockMvc.perform(post("/api/product/createProduct")
		.contentType("application/json")
		.content(objectMapper.writeValueAsString(product)))
		.andExpect(status().isBadRequest());
    }

    @Test
    public void saveProduct_withoutPrice() throws Exception {
	ProductDto product = new ProductDto();
	product.setProductName("product name");
	product.setColor("Red");
	product.setDescription("Description 1");
	product.setStockQuantity(20);
	product.setLoginId(RoleEnum.ADMIN.getCode());
	mockMvc.perform(post("/api/product/createProduct")
		.contentType("application/json")
		.content(objectMapper.writeValueAsString(product)))
		.andExpect(status().isBadRequest());
    }

    @Test
    public void saveProduct_invalidLoginId() throws Exception {
	ProductDto product = new ProductDto(20, 100.0, null, "Red", "Description 1", 20, 0L, null, RoleEnum.HEAD_OF_DEPARTMENT.getCode());
	mockMvc.perform(post("/api/product/createProduct")
		.contentType("application/json")
		.content(objectMapper.writeValueAsString(product)))
		.andExpect(status().isBadRequest());
    }

    @Test
    public void updateProductStockQuantity() throws Exception {
	ProductUpdateStockQuantityDTO product = new ProductUpdateStockQuantityDTO(42L, 20, RoleEnum.ADMIN.getCode());
	mockMvc.perform(put("/api/product/updateProductStockQuantity")
		.contentType("application/json")
		.content(objectMapper.writeValueAsString(product)))
		.andExpect(status().isOk());
    }

    @Test
    public void updateProductStockQuantity_invalidProductId() throws Exception {
	ProductUpdateStockQuantityDTO product = new ProductUpdateStockQuantityDTO(420L, 20, RoleEnum.ADMIN.getCode());
	mockMvc.perform(put("/api/product/updateProductStockQuantity")
		.contentType("application/json")
		.content(objectMapper.writeValueAsString(product)))
		.andExpect(status().isBadRequest());
    }

    @Test
    public void updateProductStockQuantity_invalidLoginId() throws Exception {
	ProductUpdateStockQuantityDTO product = new ProductUpdateStockQuantityDTO(42L, 20, RoleEnum.HEAD_OF_DEPARTMENT.getCode());
	mockMvc.perform(put("/api/product/updateProductStockQuantity")
		.contentType("application/json")
		.content(objectMapper.writeValueAsString(product)))
		.andExpect(status().isBadRequest());
    }

    @Test
    public void deleteProduct() throws Exception {
	mockMvc.perform(delete("/api/product/deleteProduct/{productId}", 60)
		.param("loginId", String.valueOf(RoleEnum.ADMIN.getCode())))
		.andExpect(status().isOk());
    }

    @Test
    public void deleteProduct_invalidtProductId() throws Exception {
	mockMvc.perform(delete("/api/product/deleteProduct/{productId}", 600)
		.param("loginId", String.valueOf(RoleEnum.ADMIN.getCode())))
		.andExpect(status().isBadRequest());
	// .andExpect(content().string("Product not found.")); //{"label":"Product not found.","params":null}

    }

    @Test
    public void deleteProduct_invalidLoginId() throws Exception {
	mockMvc.perform(delete("/api/product/deleteProduct/{productId}", 60)
		.param("loginId", String.valueOf(RoleEnum.HEAD_OF_DEPARTMENT.getCode())))
		.andExpect(status().isBadRequest());
    }

}
