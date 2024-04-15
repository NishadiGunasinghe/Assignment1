package com.lbu.lbustudent.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "enrollment")
public class Enrolment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private String courseHref;
}
