package com.application.ads.repository;

import com.application.ads.model.Periodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodoRepository extends JpaRepository<Periodo, Long> {}