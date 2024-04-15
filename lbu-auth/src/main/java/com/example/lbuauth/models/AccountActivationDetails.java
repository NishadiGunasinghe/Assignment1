package com.example.lbuauth.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "account_activation_details")
public class AccountActivationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_timestamp")
    @CreationTimestamp
    private Timestamp createdTimestamp;

    @Column(name = "updated_timestamp")
    @UpdateTimestamp
    private Timestamp updatedTimestamp;
}
