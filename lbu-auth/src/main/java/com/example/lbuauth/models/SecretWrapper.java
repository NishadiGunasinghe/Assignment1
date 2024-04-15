package com.example.lbuauth.models;

import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;

@Data
public class SecretWrapper {
    private PublicKey publicKey;
    private PrivateKey privateKey;
}
