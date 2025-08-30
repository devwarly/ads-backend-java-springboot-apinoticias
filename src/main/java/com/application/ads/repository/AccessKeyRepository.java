package com.application.ads.repository;

import com.application.ads.model.AccessKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccessKeyRepository extends JpaRepository<AccessKey, String> {
    Optional<AccessKey> findByEmailAndKey(String email, String key);
}