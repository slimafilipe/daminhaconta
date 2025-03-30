package dev.filipe.daminhaconta.repository;

import dev.filipe.daminhaconta.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ClientRepository extends JpaRepository<Client, Long> {



}
