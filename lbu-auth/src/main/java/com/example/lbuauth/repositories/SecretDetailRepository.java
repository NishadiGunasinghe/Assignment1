package com.example.lbuauth.repositories;

import com.example.lbuauth.models.SecretDetails;
import com.example.lbuauth.models.SecretWrapper;
import com.example.lbuauth.models.enums.SecretType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretDetailRepository extends CrudRepository<SecretDetails, String> {

    SecretDetails findBySecretType(SecretType secretType);

}
