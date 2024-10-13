 

package com.example.demo.model.dto;

import lombok.Data;
 
@Data
public class TaskRequestDto {
	private Integer taskId;
    private Long productId;
    private Long userId;
    private boolean isApproved;
    
    public boolean getIsApproved() {
    	return isApproved;
    }
}