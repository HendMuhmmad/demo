package com.example.demo.enums;

public enum RoleEnum {
    
    HEAD_OF_DEPARTMENT(1),     
    SUPER_ADMIN(2),            
    ADMIN(3),                           
    CUSTOMER(4);

    private final int value;

    // Constructor to set the integer value
    RoleEnum(int value) {
        this.value = value;
    }

    public int getCode() {
        return value;
    }
}
