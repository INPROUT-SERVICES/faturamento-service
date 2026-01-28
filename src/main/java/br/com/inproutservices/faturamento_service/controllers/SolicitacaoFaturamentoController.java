package br.com.inproutservices.faturamento_service.controllers;

import br.com.inproutservices.faturamento_service.dtos.*;
import br.com.inproutservices.faturamento_service.entities.SolicitacaoFaturamento;
import br.com.inproutservices.faturamento_service.services.SolicitacaoFaturamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
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

    @GetMapping("/fila-coordenador/{usuarioId}")
    public ResponseEntity<List<FilaCoordenadorDTO>> getFilaCoordinator(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.getFilaCoordinator(usuarioId));
    }

    @GetMapping("/fila-adiantamento/{usuarioId}")
    public ResponseEntity<List<FilaAdiantamentoDTO>> getFilaAdiantamento(@PathVariable Long usuarioId) {
        // Placeholder para evitar erro se o método não existir no service ainda
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/visao-adiantamentos/{usuarioId}")
    public ResponseEntity<List<VisaoAdiantamentoDTO>> getVisaoAdiantamentos(@PathVariable Long usuarioId) {
        // Placeholder
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/solicitar/{osLpuDetalheId}/{solicitanteId}")
    public ResponseEntity<SolicitacaoFaturamentoDTO> solicitar(@PathVariable Long osLpuDetalheId, @PathVariable Long solicitanteId) {
        SolicitacaoFaturamento criada = service.solicitarIdFaturamento(osLpuDetalheId, solicitanteId);
        return ResponseEntity.ok(new SolicitacaoFaturamentoDTO(criada));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<SolicitacaoFaturamentoDTO> alterarStatus(@PathVariable Long id, @RequestBody AcaoFaturamentoDTO acao) {
        SolicitacaoFaturamento atualizada = service.alterarStatus(id, acao);
        return ResponseEntity.ok(new SolicitacaoFaturamentoDTO(atualizada));
    }
}