package com.lbu.lbufinance.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.util.Locale;

@Entity
@Data
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    @NotBlank(message = "{reference.required}")
    @Size(min = 8, max = 8, message = "{reference.size}")
    @Pattern(regexp = "[A-Z0-9]*", message = "{reference.format}")
    private String reference;
    private Double amount;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")
    private Account account;

    public void populateReference() {
        this.reference = RandomStringUtils.random(8, true, true).toUpperCase(Locale.UK);
    }

}
