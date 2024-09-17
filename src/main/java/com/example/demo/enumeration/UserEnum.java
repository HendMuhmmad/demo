package com.example.demo.enumeration;

public enum UserEnum {

    HEAD_OF_DEPARTMENT(1),
    SUPER_ADMIN(2),
    ADMIN(3),
    CUSTOMER(4);

    private final int code;

    private UserEnum(int roleId) {
        this.code = roleId;
    }

    public int getCode() {
        return code;
    }
    
//    public static UserEnum fromRoleId(int roleId) {
//        for (UserEnum type : UserEnum.values()) {
//            if (type.getRoleId() == roleId) {
//                return type;
//            }
//        }
//        throw new IllegalArgumentException("Invalid roleId: " + roleId);
//    }
}
