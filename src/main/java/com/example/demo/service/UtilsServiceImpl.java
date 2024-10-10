package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.exception.BusinessException;

@Service
public class UtilsServiceImpl implements UtilsService{
	@Override
	public void validateNotNull(Object object, String paramName) {
		if (object == null) {
			throw new BusinessException(paramName + " cannot be null");
		}
	}
	@Override
	public void validateNotNullOrEmpty(String str, String paramName) {
		if (str == null || str.trim().isEmpty()) {
			throw new BusinessException(paramName + " cannot be null or empty");
		}
	}

}
