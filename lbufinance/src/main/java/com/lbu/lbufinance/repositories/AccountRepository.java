package com.lbu.lbufinance.repositories;

import com.lbu.lbufinance.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findAccountByAuthUserHref (String authUserHref);
}
