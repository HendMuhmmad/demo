package com.example.demo.model.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "ECO_ROLE")
public class Role {

	@Id
	@SequenceGenerator(name = "ECO_ROLE_SEQ", sequenceName = "ECO_ROLE_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ECO_ROLE_SEQ")
	private Long id;

	@Column(name = "ROLE_DESC", nullable = false)
	private String roleDesc;

}
