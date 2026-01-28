package br.com.inproutservices.faturamento_service.dtos.gate;

import br.com.inproutservices.faturamento_service.enums.SituacaoAprovacao;
import lombok.Data;

@Data
public class GateCreateDTO {
    private Long solicitacaoId;
    private Long usuarioId;
    private SituacaoAprovacao status;
    private String observacao;
}