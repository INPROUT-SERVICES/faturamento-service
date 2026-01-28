package br.com.inproutservices.faturamento_service.repositories;

import br.com.inproutservices.faturamento_service.entities.Gate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GateRepository extends JpaRepository<Gate, Long> {
    Optional<Gate> findBySolicitacaoId(Long solicitacaoId);
}