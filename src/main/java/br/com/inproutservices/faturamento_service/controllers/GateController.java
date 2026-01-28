package br.com.inproutservices.faturamento_service.controllers;

import br.com.inproutservices.faturamento_service.dtos.gate.GateCreateDTO;
import br.com.inproutservices.faturamento_service.dtos.gate.GateResponseDTO;
import br.com.inproutservices.faturamento_service.services.GateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gate") // Rota base: /faturamento/gate (via Nginx)
public class GateController {

    private final GateService gateService;

    public GateController(GateService gateService) {
        this.gateService = gateService;
    }

    @PostMapping
    public ResponseEntity<GateResponseDTO> registrar(@RequestBody GateCreateDTO dto) {
        GateResponseDTO response = gateService.registrarAprovacao(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/solicitacao/{solicitacaoId}")
    public ResponseEntity<GateResponseDTO> buscarPorSolicitacao(@PathVariable Long solicitacaoId) {
        return ResponseEntity.ok(gateService.buscarPorSolicitacao(solicitacaoId));
    }

    @GetMapping
    public ResponseEntity<List<GateResponseDTO>> listarTodos() {
        return ResponseEntity.ok(gateService.listarTodos());

    }

    @GetMapping("/vigente")
    public ResponseEntity<GateResponseDTO> getGateVigente() {
        return ResponseEntity.ok(gateService.buscarGateVigente());
    }
}