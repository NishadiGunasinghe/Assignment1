package com.example.lbuauth.repositories;

import com.example.lbuauth.models.UserMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMetaDataRepository extends JpaRepository<UserMetaData, String> {
}

