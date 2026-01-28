package br.com.inproutservices.faturamento_service.dtos.integration;
import lombok.Data;

@Data
public class ItemCandidatoDTO {
    private Long osLpuDetalheId;
    private String numeroOs;
    private String descricaoItem;
    private String segmento;
    private String statusOperacional;
}