package com.example.demo.model.dto;

import lombok.Data;

@Data 
public class TaskRequestDto {
	private Long taskId;
    private Long userId;
    private boolean isApproved;
    private String notes;
    private String refuseReason;
    
    
    public boolean getIsApproved() {
        return isApproved;
    }
}
