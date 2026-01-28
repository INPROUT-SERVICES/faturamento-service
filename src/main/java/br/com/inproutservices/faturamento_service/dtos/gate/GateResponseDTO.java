package br.com.inproutservices.faturamento_service.dtos.gate;

import br.com.inproutservices.faturamento_service.entities.Gate;
import br.com.inproutservices.faturamento_service.enums.SituacaoAprovacao;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GateResponseDTO {
    private Long id;
    private Long solicitacaoId;
    private Long usuarioResponsavelId;
    private SituacaoAprovacao status;
    private String observacao;
    private LocalDateTime dataAcao;

    public GateResponseDTO(Gate entity) {
        this.id = entity.getId();
        this.usuarioResponsavelId = entity.getUsuarioResponsavelId();
        this.status = entity.getStatus();
        this.observacao = entity.getObservacao();
        this.dataAcao = entity.getDataAcao();
        if (entity.getSolicitacao() != null) {
            this.solicitacaoId = entity.getSolicitacao().getId();
        }
    }
}