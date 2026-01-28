package br.com.inproutservices.faturamento_service.services;

import br.com.inproutservices.faturamento_service.clients.MonolitoClient;
import br.com.inproutservices.faturamento_service.dtos.integration.ItemCandidatoDTO;
import br.com.inproutservices.faturamento_service.dtos.integration.OsLpuDetalheDTO;
import br.com.inproutservices.faturamento_service.dtos.integration.UsuarioDTO;
import br.com.inproutservices.faturamento_service.entities.SolicitacaoFaturamento;
import br.com.inproutservices.faturamento_service.enums.StatusFaturamento;
import br.com.inproutservices.faturamento_service.enums.TipoFaturamento;
import br.com.inproutservices.faturamento_service.repositories.SolicitacaoFaturamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolicitacaoFaturamentoService {

    private final SolicitacaoFaturamentoRepository repo;
    private final MonolitoClient monolitoClient;

    public SolicitacaoFaturamentoService(SolicitacaoFaturamentoRepository repo, MonolitoClient monolitoClient) {
        this.repo = repo;
        this.monolitoClient = monolitoClient;
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoFaturamentoDTO> getFilaAssistant(Long usuarioId) {
        // 1. Busca dados do usuário no Monólito
        UsuarioDTO usuario = monolitoClient.getUsuario(usuarioId);

        List<StatusFaturamento> statuses = List.of(StatusFaturamento.PENDENTE_ASSISTANT, StatusFaturamento.ID_RECEBIDO, StatusFaturamento.ID_RECUSADO);
        List<SolicitacaoFaturamento> lista;

        // 2. Aplica filtro de segmento baseado nos IDs recebidos
        if ("COORDINATOR".equals(usuario.getRole())) {
            if (usuario.getSegmentosIds() == null || usuario.getSegmentosIds().isEmpty()) return List.of();
            lista = repo.findByStatusInAndSegmentoIdIn(statuses, usuario.getSegmentosIds());
        } else {
            lista = repo.findByStatusIn(statuses);
        }

        return lista.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<FilaCoordenadorDTO> getFilaCoordinator(Long usuarioId) {
        // Chama endpoint do Monólito que calcula as pendências operacionais
        List<ItemCandidatoDTO> candidatos = monolitoClient.getItensCandidatos(usuarioId);

        // Remove os que JÁ têm solicitação no nosso banco local
        return candidatos.stream()
                .filter(c -> !repo.existsByOsLpuDetalheId(c.getOsLpuDetalheId()))
                .map(c -> {
                    FilaCoordenadorDTO dto = new FilaCoordenadorDTO();
                    dto.setOsLpuDetalheId(c.getOsLpuDetalheId());
                    dto.setNumeroOs(c.getNumeroOs());
                    dto.setDescricaoItem(c.getDescricaoItem());
                    dto.setSegmento(c.getSegmento());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DashboardFaturamentoDTO getDashboardFaturamento(Long usuarioId) {
        UsuarioDTO usuario = monolitoClient.getUsuario(usuarioId);
        boolean isCoordinator = "COORDINATOR".equals(usuario.getRole());

        long pendenteSolicitacao = 0;
        if (isCoordinator) {
            // Se for coordenador, consulta API do monólito
            pendenteSolicitacao = getFilaCoordinator(usuarioId).size();
        }

        List<SolicitacaoFaturamento> baseQuery;
        if (isCoordinator) {
            baseQuery = repo.findBySegmentoIdIn(usuario.getSegmentosIds());
        } else {
            baseQuery = repo.findAll();
        }

        long pendenteFila = baseQuery.stream().filter(s -> s.getStatus() == StatusFaturamento.PENDENTE_ASSISTANT).count();
        long idsRecusados = baseQuery.stream().filter(s -> s.getStatus() == StatusFaturamento.ID_RECUSADO).count();

        // Correção no filtro de adiantamentos
        long adiantamentos = baseQuery.stream()
                .filter(s -> s.getTipo() == TipoFaturamento.ADIANTAMENTO && s.getStatus() != StatusFaturamento.FATURADO)
                .count();

        LocalDateTime trintaDiasAtras = LocalDateTime.now().minusDays(30);
        long faturadoMes = baseQuery.stream()
                .filter(s -> s.getStatus() == StatusFaturamento.FATURADO && s.getDataUltimaAcao().isAfter(trintaDiasAtras))
                .count();

        return new DashboardFaturamentoDTO(pendenteSolicitacao, pendenteFila, idsRecusados, adiantamentos, faturadoMes);
    }

    @Transactional
    public SolicitacaoFaturamento solicitarIdFaturamento(Long osLpuDetalheId, Long solicitanteId) {
        if (repo.existsByOsLpuDetalheId(osLpuDetalheId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe solicitação.");
        }

        // Busca detalhes no Monólito para salvar o segmentoId
        OsLpuDetalheDTO detalhe = monolitoClient.getDetalhe(osLpuDetalheId);

        SolicitacaoFaturamento sol = new SolicitacaoFaturamento();
        sol.setOsLpuDetalheId(osLpuDetalheId);
        sol.setSolicitanteId(solicitanteId);
        sol.setSegmentoId(detalhe.getSegmentoId());
        sol.setStatus(StatusFaturamento.PENDENTE_ASSISTANT);
        sol.setTipo(TipoFaturamento.REGULAR);

        return repo.save(sol);
    }

    @Transactional
    public SolicitacaoFaturamento alterarStatus(Long solicitacaoId, AcaoFaturamentoDTO acao) {
        SolicitacaoFaturamento solicitacao = repo.findById(solicitacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada."));

        if (acao.getNovoStatus() == StatusFaturamento.ID_RECUSADO && (acao.getMotivo() == null || acao.getMotivo().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Motivo é obrigatório para recusar.");
        }

        solicitacao.setStatus(acao.getNovoStatus());
        solicitacao.setResponsavelId(acao.getUsuarioId());
        solicitacao.setDataUltimaAcao(LocalDateTime.now());

        if (acao.getNovoStatus() == StatusFaturamento.ID_RECUSADO) {
            solicitacao.setObservacao(acao.getMotivo());
        }

        return repo.save(solicitacao);
    }

    private SolicitacaoFaturamentoDTO toDTO(SolicitacaoFaturamento entity) {
        SolicitacaoFaturamentoDTO dto = new SolicitacaoFaturamentoDTO();
        dto.setId(entity.getId());
        dto.setOsLpuDetalheId(entity.getOsLpuDetalheId());
        dto.setStatus(entity.getStatus());
        dto.setTipo(entity.getTipo());
        dto.setDataSolicitacao(entity.getDataSolicitacao());
        dto.setDataUltimaAcao(entity.getDataUltimaAcao());
        dto.setObservacao(entity.getObservacao());
        return dto;
    }
}