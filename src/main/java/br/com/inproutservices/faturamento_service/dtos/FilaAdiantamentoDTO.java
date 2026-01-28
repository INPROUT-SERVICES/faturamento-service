package br.com.inproutservices.faturamento_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilaAdiantamentoDTO {
    private Long osLpuDetalheId;
    private String numeroOs;
    private String descricaoItem;
    private String statusAtual;
}