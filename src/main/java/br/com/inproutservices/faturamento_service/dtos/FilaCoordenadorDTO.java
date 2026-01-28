package br.com.inproutservices.faturamento_service.dtos;

import lombok.Data;

@Data
public class FilaCoordenadorDTO {
    private Long osLpuDetalheId;
    private String numeroOs;
    private String descricaoItem;
    private String segmento;
}