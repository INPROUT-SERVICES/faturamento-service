package br.com.inproutservices.faturamento_service.dtos;

import br.com.inproutservices.faturamento_service.entities.SolicitacaoFaturamento;
import br.com.inproutservices.faturamento_service.enums.StatusFaturamento;
import br.com.inproutservices.faturamento_service.enums.TipoFaturamento;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SolicitacaoFaturamentoDTO {
    private Long id;
    private Long osLpuDetalheId;
    private String numeroOs;
    private String descricaoItem;
    private String solicitanteNome;
    private String responsavelNome;
    private StatusFaturamento status;
    private TipoFaturamento tipo;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataUltimaAcao;
    private String observacao;
    private Double valor;

    public SolicitacaoFaturamentoDTO(SolicitacaoFaturamento entity) {
        this.id = entity.getId();
        this.osLpuDetalheId = entity.getOsLpuDetalheId();
        this.status = entity.getStatus();
        this.tipo = entity.getTipo();
        this.dataSolicitacao = entity.getDataSolicitacao();
        this.dataUltimaAcao = entity.getDataUltimaAcao();
        this.observacao = entity.getObservacao();
    }
}