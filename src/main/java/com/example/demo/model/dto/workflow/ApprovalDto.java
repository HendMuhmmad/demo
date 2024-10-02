package com.example.demo.model.dto.workflow;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalDto {
    @NotNull
    private Long taskId;

    @NotNull
    private Long loginId;

    private String note;
    
}
