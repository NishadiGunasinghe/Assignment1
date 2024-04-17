package com.example.lbuauth.models.enums;

import lombok.Getter;

// Enum to represent different types of secrets
@Getter
public enum SecretType {

    JWT_SECRET("JWT_SECRET");

    private final String secretName;

    SecretType(String secretName) {

        this.secretName = secretName;
    }
}
