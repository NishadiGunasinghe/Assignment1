package com.lbu.lbustudent.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    private String authUserHref;
    @Column(length = 5000)
    private String address;
    @Column(length = 11)
    private String emergencyContact;
    private String dateOfBirth;
    @Column(length = 11)
    private String phoneContact;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrolment> enrollments;

    @Column(name = "created_timestamp")
    @CreationTimestamp
    private Timestamp createdTimestamp;

    @Column(name = "updated_timestamp")
    @UpdateTimestamp
    private Timestamp updatedTimestamp;
}
