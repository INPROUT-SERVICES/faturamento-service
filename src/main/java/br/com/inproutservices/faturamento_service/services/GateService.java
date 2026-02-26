package br.com.inproutservices.faturamento_service.services;

import br.com.inproutservices.faturamento_service.dtos.gate.GateCreateDTO;
import br.com.inproutservices.faturamento_service.dtos.gate.GateResponseDTO;
import br.com.inproutservices.faturamento_service.dtos.gate.GateUpdateDTO;
import br.com.inproutservices.faturamento_service.entities.Gate;
import br.com.inproutservices.faturamento_service.repositories.GateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GateService {

    private final GateRepository gateRepository;

    public GateService(GateRepository gateRepository) {
        this.gateRepository = gateRepository;
    }

    @Transactional
    public GateResponseDTO criar(GateCreateDTO dto) {
        Gate gate = new Gate();
        gate.setNome(dto.nome());
        gate.setDataInicio(dto.dataInicio());
        gate.setDataFim(dto.dataFim());
        gate.setCriadoPorUsuarioId(dto.usuarioId());

        Gate salvo = gateRepository.save(gate);
        return new GateResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public GateResponseDTO buscarPorId(Long id) {
        Gate gate = gateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gate não encontrado com o ID: " + id));
        return new GateResponseDTO(gate);
    }

    @Transactional(readOnly = true)
    public List<GateResponseDTO> listarTodos() {
        return gateRepository.findAll().stream()
                .map(GateResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public GateResponseDTO atualizar(Long id, GateUpdateDTO dto) {
        Gate gate = gateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gate não encontrado com o ID: " + id));

        gate.setNome(dto.nome());
        gate.setDataInicio(dto.dataInicio());
        gate.setDataFim(dto.dataFim());
        gate.setAlteradoPorUsuarioId(dto.usuarioId());

        Gate atualizado = gateRepository.save(gate);
        return new GateResponseDTO(atualizado);
    }

    @Transactional
    public void excluir(Long id) {
        if (!gateRepository.existsById(id)) {
            throw new EntityNotFoundException("Gate não encontrado com o ID: " + id);
        }
        gateRepository.deleteById(id);
    }
}