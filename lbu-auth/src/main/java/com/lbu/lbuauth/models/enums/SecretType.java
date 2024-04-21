package com.lbu.lbuauth.models.enums;

import lombok.Getter;

@Getter
public enum SecretType {

    JWT_SECRET("JWT_SECRET");

    private final String secretName;

    SecretType(String secretName) {

        this.secretName = secretName;
    }
}
