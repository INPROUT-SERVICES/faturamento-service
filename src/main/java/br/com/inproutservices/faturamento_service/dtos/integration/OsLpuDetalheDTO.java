package br.com.inproutservices.faturamento_service.dtos.integration;
import lombok.Data;

@Data
public class OsLpuDetalheDTO {
    private Long id;
    private String descricao;
    private Double precoTotalInprout;
    private String numeroOs;
    private Long segmentoId;
    private String segmentoNome;
}