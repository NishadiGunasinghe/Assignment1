package com.lbu.lbuauth.repositories;

import com.lbu.lbuauth.models.SecretDetails;
import com.lbu.lbuauth.models.enums.SecretType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretDetailRepository extends CrudRepository<SecretDetails, String> {

    SecretDetails findBySecretType(SecretType secretType);

}
