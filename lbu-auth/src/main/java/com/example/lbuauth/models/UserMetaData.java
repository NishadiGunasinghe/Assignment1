package com.example.lbuauth.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "user_metadata")
public class UserMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Timestamp lastLoginTimestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_timestamp")
    @CreationTimestamp
    private Timestamp createdTimestamp;

    @Column(name = "updated_timestamp")
    @UpdateTimestamp
    private Timestamp updatedTimestamp;
}
