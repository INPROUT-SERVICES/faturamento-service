package br.com.inproutservices.faturamento_service.repositories;

import br.com.inproutservices.faturamento_service.entities.Gate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GateRepository extends JpaRepository<Gate, Long> {

}