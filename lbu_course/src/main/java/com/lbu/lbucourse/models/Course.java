package com.lbu.lbucourse.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    private String title;
    @Column(length = 5000)
    private String description;
    @Column(precision = 10, scale = 2)
    private BigDecimal fees;
    private Integer durationInDays;
    private String instructor;
}
