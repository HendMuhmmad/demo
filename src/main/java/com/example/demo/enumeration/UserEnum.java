package com.example.demo.enumeration;

public enum UserEnum {

    HEAD_OF_DEPARTMENT(1),
    SUPER_ADMIN(2),
    ADMIN(3),
    CUSTOMER(4);

    private final int roleId;

    private UserEnum(int roleId) {
        this.roleId = roleId;
    }

    public int getRoleId() {
        return roleId;
    }
    
    public static UserEnum fromRoleId(int roleId) {
        for (UserEnum type : UserEnum.values()) {
            if (type.getRoleId() == roleId) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid roleId: " + roleId);
    }
}
