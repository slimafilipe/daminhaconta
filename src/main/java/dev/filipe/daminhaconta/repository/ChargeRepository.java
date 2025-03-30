package dev.filipe.daminhaconta.repository;

import dev.filipe.daminhaconta.model.Client;
import dev.filipe.daminhaconta.model.Charge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
    List<Charge> findByClientAndIssueDateBetween(Client client, LocalDateTime start, LocalDateTime end);
    List<Charge> findByStatus(String status);
}

