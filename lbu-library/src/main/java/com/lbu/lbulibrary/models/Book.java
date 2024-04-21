package com.lbu.lbulibrary.models;

import jakarta.persistence.*;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Data
@Entity
@Table(name = "book", uniqueConstraints = {@UniqueConstraint(columnNames = {"title", "author", "year"})})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    @Column(nullable = false)
    private Integer yearOfPublished;

    private Integer copies;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactions = new HashSet<>();

    @Column(name = "created_timestamp")
    @CreationTimestamp
    private Timestamp createdTimestamp;

    @Column(name = "updated_timestamp")
    @UpdateTimestamp
    private Timestamp updatedTimestamp;

    public void populateIsbn() {
        this.isbn = RandomStringUtils.random(8, true, true).toUpperCase(Locale.UK);
    }
}
