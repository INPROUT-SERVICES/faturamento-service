package br.com.inproutservices.faturamento_service.dtos.gate;

import java.time.LocalDateTime;

public record GateUpdateDTO(
        String nome,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        Long usuarioId
) {}