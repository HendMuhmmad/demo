package com.example.demo.model.orm.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "ECO_ACTIONS")
public class WFActions {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "ACTION_NAME")
    private String actionName;
}