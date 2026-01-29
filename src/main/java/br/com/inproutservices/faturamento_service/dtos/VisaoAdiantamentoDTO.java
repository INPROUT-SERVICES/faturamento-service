package br.com.inproutservices.faturamento_service.dtos;

import br.com.inproutservices.faturamento_service.entities.SolicitacaoFaturamento;
import br.com.inproutservices.faturamento_service.enums.StatusFaturamento;
import lombok.Data;
import lombok.NoArgsConstructor; // Adicionado
import lombok.AllArgsConstructor; // Adicionado
import java.time.LocalDateTime; // Importante para a data

@Data
@NoArgsConstructor // Necessário para usar 'new VisaoAdiantamentoDTO()'
@AllArgsConstructor
public class VisaoAdiantamentoDTO {
    private Long solicitacaoId;
    private String numeroOs;
    private String descricaoItem;
    private StatusFaturamento statusFaturamento;
    private LocalDateTime dataSolicitacao; // Campo que faltava
    private boolean itemFinalizadoOperacionalmente;

    public VisaoAdiantamentoDTO(SolicitacaoFaturamento sf, boolean finalizado) {
        this.solicitacaoId = sf.getId();
        this.statusFaturamento = sf.getStatus();
        this.dataSolicitacao = sf.getDataSolicitacao(); // Mapeando a data
        this.itemFinalizadoOperacionalmente = finalizado;
    }
}