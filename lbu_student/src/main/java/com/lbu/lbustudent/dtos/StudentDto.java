package com.lbu.lbustudent.dtos;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class StudentDto {
    private String id;
    private String address;
    private String emergencyContact;
    private String dateOfBirth;
    private String phoneContact;
    private String authUserHref;
    private Timestamp createdTimestamp;
    private Timestamp updatedTimestamp;
    private List<String> courseHrefs;
}