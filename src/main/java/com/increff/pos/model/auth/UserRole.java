package com.increff.pos.model.auth;

public enum UserRole {

    OPERATOR, SUPERVISOR, NONE;

//    public static UserRole getRole(String roleString) {
//        roleString = roleString.trim();
//        if (roleString.equalsIgnoreCase(OPERATOR.toString())) {
//            return OPERATOR;
//        }
//        if (roleString.equalsIgnoreCase(SUPERVISOR.toString())) {
//            return OPERATOR;
//        }
//        return NONE;
//    }

}