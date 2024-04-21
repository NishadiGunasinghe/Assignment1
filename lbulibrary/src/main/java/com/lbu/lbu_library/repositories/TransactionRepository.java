package com.lbu.lbu_library.repositories;

import com.lbu.lbu_library.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

        List<Transaction> findAllByStudent_AuthUserHrefAndDateReturnedIsNull(String authUserHref);

        @Query(value = "select * from transaction t where t.date_returned is null  and DATE_ADD(t.date_borrowed, INTERVAL :bookReturnSeconds SECOND) < CURRENT_TIMESTAMP", nativeQuery = true)
        List<Transaction> findAllDateReturnedIsNull(Long bookReturnSeconds);
        List<Transaction> findAllByBook_IsbnAndDateReturnedIsNull(String isbn);

}
