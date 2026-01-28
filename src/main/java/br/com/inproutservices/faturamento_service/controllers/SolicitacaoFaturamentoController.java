package br.com.inproutservices.faturamento_service.controllers;

import br.com.inproutservices.faturamento_service.dtos.*;
import br.com.inproutservices.faturamento_service.dtos.integration.FilaCoordenadorDTO;
import br.com.inproutservices.faturamento_service.entities.SolicitacaoFaturamento;
import br.com.inproutservices.faturamento_service.services.SolicitacaoFaturamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/") // O Nginx já entrega em /faturamento, então aqui é raiz
public class SolicitacaoFaturamentoController {

    private final SolicitacaoFaturamentoService service;

    public SolicitacaoFaturamentoController(SolicitacaoFaturamentoService service) {
        this.service = service;
    }

    @GetMapping("/dashboard/{usuarioId}")
    public ResponseEntity<DashboardFaturamentoDTO> getDashboard(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.getDashboardFaturamento(usuarioId));
    }

    @GetMapping("/fila-assistant/{usuarioId}")
    public ResponseEntity<List<SolicitacaoFaturamentoDTO>> getFilaAssistant(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.getFilaAssistant(usuarioId));
    }

    // Este endpoint chama o Monólito para saber o que está pendente
    @GetMapping("/fila-coordenador/{usuarioId}")
    public ResponseEntity<List<FilaCoordenadorDTO>> getFilaCoordinator(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.getFilaCoordinator(usuarioId));
    }

    @PostMapping("/solicitar/{osLpuDetalheId}/{solicitanteId}")
    public ResponseEntity<SolicitacaoFaturamentoDTO> solicitar(@PathVariable Long osLpuDetalheId, @PathVariable Long solicitanteId) {
        SolicitacaoFaturamento criada = service.solicitarIdFaturamento(osLpuDetalheId, solicitanteId);
        // Retornamos um DTO simples convertido manualmente ou via helper
        return ResponseEntity.ok(new SolicitacaoFaturamentoDTO(criada));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<SolicitacaoFaturamentoDTO> alterarStatus(@PathVariable Long id, @RequestBody AcaoFaturamentoDTO acao) {
        SolicitacaoFaturamento atualizada = service.alterarStatus(id, acao);
        return ResponseEntity.ok(new SolicitacaoFaturamentoDTO(atualizada));
    }
}