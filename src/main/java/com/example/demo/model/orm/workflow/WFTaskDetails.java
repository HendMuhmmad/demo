package com.example.demo.model.orm.workflow;
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ECO_VW_WF_TASK_DETAILS")
public class WFTaskDetails {
 
    @Id
    @Column(name = "TASK_ID")
    private Long taskId;
 
    @Column(name = "INSTANCE_ID")
    private Long instanceId;
 
    @Column(name = "ASSIGNEE_ID")
    private Long assigneeId;
 
    @Column(name = "ASSIGNEE_ROLE")
    private String assigneeRole;
 
    @Column(name = "ASSIGN_DATE")
    private java.util.Date assignDate;
 
    @Column(name = "ACTION")
    private String action;
 
    @Column(name = "ACTION_DATE")
    private java.util.Date actionDate;
 
    @Column(name = "NOTES")
    private String notes;
 
    @Column(name = "REFUSE_REASONS")
    private String refuseReasons;
 
    @Column(name = "PRODUCT_NAME")
    private String productName;
 
    @Column(name = "PRICE")
    private Double price;
 
    @Column(name = "COLOR")
    private String color;
 
    @Column(name = "STOCK_QUANTITY")
    private Integer stockQuantity;
 
    @Column(name = "DESCRIPTION")
    private String description;
 
    @Column(name = "STATUS")
    private Integer status;
}