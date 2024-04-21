package com.lbu.lbuauth.models.enums;

import lombok.Getter;

@Getter
public enum RoleType {

    USER("ROLE_GENERAL_USER", 0),
    STUDENT("ROLE_STUDENT", 1),
    ADMIN("ROLE_ADMIN", 3);

    private final String stringRoleType;
    private final Integer roleNumber;

    RoleType(String stringRoleType, Integer roleNumber) {
        this.roleNumber = roleNumber;
        this.stringRoleType = stringRoleType;
    }
}
