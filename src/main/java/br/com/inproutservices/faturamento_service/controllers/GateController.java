package br.com.inproutservices.faturamento_service.controllers;

import br.com.inproutservices.faturamento_service.dtos.gate.GateCreateDTO;
import br.com.inproutservices.faturamento_service.dtos.gate.GateResponseDTO;
import br.com.inproutservices.faturamento_service.dtos.gate.GateUpdateDTO;
import br.com.inproutservices.faturamento_service.services.GateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gates")
public class GateController {

    private final GateService gateService;

    public GateController(GateService gateService) {
        this.gateService = gateService;
    }

    @PostMapping
    public ResponseEntity<GateResponseDTO> criar(@RequestBody GateCreateDTO dto) {
        GateResponseDTO response = gateService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GateResponseDTO>> listarTodos() {
        return ResponseEntity.ok(gateService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GateResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(gateService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GateResponseDTO> atualizar(@PathVariable Long id, @RequestBody GateUpdateDTO dto) {
        return ResponseEntity.ok(gateService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        gateService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}