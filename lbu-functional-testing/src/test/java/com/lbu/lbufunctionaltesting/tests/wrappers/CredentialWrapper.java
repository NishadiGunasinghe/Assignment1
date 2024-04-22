package com.lbu.lbufunctionaltesting.tests.wrappers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CredentialWrapper {

    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String email;

}
