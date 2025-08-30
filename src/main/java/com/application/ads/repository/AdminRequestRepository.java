package com.application.ads.repository;

import com.application.ads.model.AdminRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminRequestRepository extends JpaRepository<AdminRequest, Long> {
    Optional<AdminRequest> findByEmail(String email);
}