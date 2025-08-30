package com.application.ads.repository;

import com.application.ads.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Importe a anotação

import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    Optional<Subscriber> findByEmail(String email);

    // O Spring Data JPA precisa desta anotação para a operação de delete
    @Transactional
    void deleteByEmail(String email);
}