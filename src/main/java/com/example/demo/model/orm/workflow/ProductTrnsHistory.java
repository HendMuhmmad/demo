package com.example.demo.model.orm.workflow;
 
import java.util.Date;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ECO_PRODUCT_TRNS_HISTORY")
public class ProductTrnsHistory {
 
    @Id
    @SequenceGenerator(name = "ECO_PRODUCT_TRNS_HISTORY_SEQ",
    sequenceName = "ECO_PRODUCT_TRNS_HISTORY_SEQ",
    allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "ECO_PRODUCT_TRNS_HISTORY_SEQ")
    private Long id;
 
    @Column(name = "PRODUCT_ID")
    private Long productId;
 
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
 
    @Column(name = "CREATION_DATE")
    @Temporal(TemporalType.DATE)
    private Date creationDate;
 
    @Column(name = "STATUS")
    private Long status;
 
}