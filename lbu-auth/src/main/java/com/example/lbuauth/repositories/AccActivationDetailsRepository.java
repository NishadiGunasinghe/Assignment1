package com.example.lbuauth.repositories;

import com.example.lbuauth.models.AccountActivationDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccActivationDetailsRepository extends CrudRepository<AccountActivationDetails, String> {

    Optional<AccountActivationDetails> findByUser_Username(String username);

    Optional<AccountActivationDetails> findByToken(String token);

    Optional<AccountActivationDetails> findByUser_Id(String userId);
}
