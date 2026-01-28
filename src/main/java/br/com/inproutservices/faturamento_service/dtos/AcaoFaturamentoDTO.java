package br.com.inproutservices.faturamento_service.dtos;

import br.com.inproutservices.faturamento_service.enums.StatusFaturamento;
import lombok.Data;

@Data
public class AcaoFaturamentoDTO {
    private Long usuarioId;
    private StatusFaturamento novoStatus;
    private String motivo;
}