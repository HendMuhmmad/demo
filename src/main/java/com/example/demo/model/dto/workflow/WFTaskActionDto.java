package com.example.demo.model.dto.workflow;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WFTaskActionDto {
    private Long taskId;
    private Long actionId;
    private String notes;
    private String refuseNotes;
    private Long loginId;


}
