package br.com.inproutservices.faturamento_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardFaturamentoDTO {
    private long pendenteSolicitacao;
    private long pendenteFila;
    private long idsRecusados;
    private long adiantamentosPendentes;
    private long faturadoMes;
}