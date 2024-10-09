package com.example.demo.model.dto.workflow;

import javax.validation.constraints.NotNull;

import com.example.demo.enums.workflow.WFActionEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto {
    @NotNull
    private Long taskId;

    @NotNull
    private Long loginId;

    private String note;

    private String rejectionReason;
    
    private String response;
    
}