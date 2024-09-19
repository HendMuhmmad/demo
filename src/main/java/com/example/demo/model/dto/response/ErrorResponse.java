package com.example.demo.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String label;
    private Object[] params;

    public ErrorResponse(String label) {
	this.label = label;
    }

}