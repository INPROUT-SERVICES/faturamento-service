package br.com.inproutservices.faturamento_service.dtos;

import br.com.inproutservices.faturamento_service.entities.SolicitacaoFaturamento;
import br.com.inproutservices.faturamento_service.enums.StatusFaturamento;
import lombok.Data;

@Data
public class VisaoAdiantamentoDTO {
    private Long solicitacaoId;
    private String numeroOs;
    private String descricaoItem;
    private StatusFaturamento statusFaturamento;
    private boolean itemFinalizadoOperacionalmente;

    public VisaoAdiantamentoDTO(SolicitacaoFaturamento sf, boolean finalizado) {
        this.solicitacaoId = sf.getId();
        this.statusFaturamento = sf.getStatus();
        this.itemFinalizadoOperacionalmente = finalizado;
        // Os dados de OS/Descrição seriam preenchidos via integração se necessário
    }
}