package br.com.inproutservices.faturamento_service.services;

import br.com.inproutservices.faturamento_service.clients.MonolitoClient;
import br.com.inproutservices.faturamento_service.dtos.gate.GateCreateDTO;
import br.com.inproutservices.faturamento_service.dtos.gate.GateResponseDTO;
import br.com.inproutservices.faturamento_service.entities.Gate;
import br.com.inproutservices.faturamento_service.entities.SolicitacaoFaturamento;
import br.com.inproutservices.faturamento_service.repositories.GateRepository;
import br.com.inproutservices.faturamento_service.repositories.SolicitacaoFaturamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GateService {

    private final GateRepository gateRepository;
    private final SolicitacaoFaturamentoRepository solicitacaoRepository;
    private final MonolitoClient monolitoClient;

    public GateService(GateRepository gateRepository,
                       SolicitacaoFaturamentoRepository solicitacaoRepository,
                       MonolitoClient monolitoClient) {
        this.gateRepository = gateRepository;
        this.solicitacaoRepository = solicitacaoRepository;
        this.monolitoClient = monolitoClient;
    }

    @Transactional
    public GateResponseDTO registrarAprovacao(GateCreateDTO dto) {
        // 1. Busca a solicitação localmente
        SolicitacaoFaturamento solicitacao = solicitacaoRepository.findById(dto.getSolicitacaoId())
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de Faturamento não encontrada com ID: " + dto.getSolicitacaoId()));

        // 2. Valida usuário no Monólito (Opcional, mas garante consistência)
        try {
            monolitoClient.getUsuario(dto.getUsuarioId());
        } catch (Exception e) {
            // Se o usuário não existir ou o monólito estiver fora, podemos decidir lançar erro ou seguir apenas com o ID
            // throw new EntityNotFoundException("Usuário inválido ou serviço indisponível.");
        }

        // 3. Verifica se já existe Gate para essa solicitação (Regra de Negócio: Atualiza ou Cria?)
        // Aqui assumimos que cria ou atualiza o existente
        Gate gate = gateRepository.findBySolicitacaoId(dto.getSolicitacaoId())
                .orElse(new Gate());

        gate.setSolicitacao(solicitacao);
        gate.setUsuarioResponsavelId(dto.getUsuarioId()); // Salvamos o ID remoto
        gate.setStatus(dto.getStatus());
        gate.setObservacao(dto.getObservacao());
        gate.setDataAcao(LocalDateTime.now());

        Gate salvo = gateRepository.save(gate);
        return new GateResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public GateResponseDTO buscarPorSolicitacao(Long solicitacaoId) {
        Gate gate = gateRepository.findBySolicitacaoId(solicitacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum registro de Gate encontrado para a solicitação: " + solicitacaoId));
        return new GateResponseDTO(gate);
    }

    @Transactional(readOnly = true)
    public List<GateResponseDTO> listarTodos() {
        return gateRepository.findAll().stream()
                .map(GateResponseDTO::new)
                .collect(Collectors.toList());
    }

    public GateResponseDTO buscarGateVigente() {
        LocalDate hoje = LocalDate.now();
        // Lógica trazida do antigo sistema
        return gateRepository.findAll().stream()
                .filter(g -> !hoje.isBefore(g.getDataInicio().toLocalDate()) && !hoje.isAfter(g.getDataFim().toLocalDate())) // Ajuste conforme seu tipo de data (LocalDate ou LocalDateTime)
                .findFirst()
                .map(GateResponseDTO::new)
                .orElse(null);
    }
}