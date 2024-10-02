package com.example.demo.model.orm.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ECO_PROCESSES_GROUPS")
public class WFProcessGroup {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String processGroupName;
}
