package br.com.inproutservices.faturamento_service.dtos.gate;

import br.com.inproutservices.faturamento_service.entities.Gate;
import java.time.LocalDateTime;

public record GateResponseDTO(
        Long id,
        String nome,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        Long criadoPorUsuarioId,
        LocalDateTime criadoEm,
        Long alteradoPorUsuarioId,
        LocalDateTime alteradoEm
) {
    public GateResponseDTO(Gate entity) {
        this(
                entity.getId(),
                entity.getNome(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getCriadoPorUsuarioId(),
                entity.getCriadoEm(),
                entity.getAlteradoPorUsuarioId(),
                entity.getAlteradoEm()
        );
    }
}