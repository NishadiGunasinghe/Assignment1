package com.lbu.lbufinance.repositories;

import com.lbu.lbufinance.models.Invoice;
import com.lbu.lbufinance.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    List<Invoice> findInvoiceByAccount_IdAndStatus(String accountId, Status status);
    Optional<Invoice> findInvoiceByReference(String reference);

    List<Invoice> findAllByAccount_AuthUserHref(String authUserHref);
}
