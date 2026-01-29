package br.com.inproutservices.faturamento_service.services;

import br.com.inproutservices.faturamento_service.clients.MonolitoClient;
import br.com.inproutservices.faturamento_service.dtos.integration.ItemCandidatoDTO;
import br.com.inproutservices.faturamento_service.dtos.integration.OsLpuDetalheDTO;
import br.com.inproutservices.faturamento_service.dtos.integration.UsuarioDTO;
import br.com.inproutservices.faturamento_service.dtos.*;
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

    // --- FILAS ---

    @Transactional(readOnly = true)
    public List<SolicitacaoFaturamentoDTO> getFilaAssistant(Long usuarioId) {
        UsuarioDTO usuario = monolitoClient.getUsuario(usuarioId);
        List<StatusFaturamento> statuses = List.of(StatusFaturamento.PENDENTE_ASSISTANT, StatusFaturamento.ID_RECEBIDO, StatusFaturamento.ID_RECUSADO);
        List<SolicitacaoFaturamento> lista;

        if ("COORDINATOR".equals(usuario.getRole())) {
            // Coordinator vê a fila mas não atua (apenas vê seus segmentos)
            if (usuario.getSegmentosIds() == null || usuario.getSegmentosIds().isEmpty()) return List.of();
            lista = repo.findByStatusInAndSegmentoIdIn(statuses, usuario.getSegmentosIds());
        } else {
            // Assistant, Admin, Controller veem tudo
            lista = repo.findByStatusIn(statuses);
        }
        return lista.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<FilaCoordenadorDTO> getFilaCoordinator(Long usuarioId) {
        // Busca itens FINALIZADOS no monólito
        List<ItemCandidatoDTO> candidatos = monolitoClient.getItensCandidatos(usuarioId);
        return candidatos.stream()
                .filter(c -> !repo.existsByOsLpuDetalheId(c.getOsLpuDetalheId()))
                .map(this::toFilaDTO)
                .collect(Collectors.toList());
    }

    public List<FilaAdiantamentoDTO> getFilaAdiantamento(Long usuarioId) {
        // Busca itens EM ANDAMENTO no monólito (implementado no passo 1)
        List<ItemCandidatoDTO> candidatos = monolitoClient.getItensCandidatosAdiantamento(usuarioId);
        return candidatos.stream()
                .filter(c -> !repo.existsByOsLpuDetalheId(c.getOsLpuDetalheId()))
                .map(c -> {
                    FilaAdiantamentoDTO dto = new FilaAdiantamentoDTO();
                    dto.setOsLpuDetalheId(c.getOsLpuDetalheId());
                    dto.setNumeroOs(c.getNumeroOs());
                    dto.setDescricaoItem(c.getDescricaoItem());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VisaoAdiantamentoDTO> getVisaoAdiantamentos(Long usuarioId) {
        // Retorna todos os adiantamentos já solicitados
        List<SolicitacaoFaturamento> adiantamentos = repo.findAll().stream()
                .filter(s -> s.getTipo() == TipoFaturamento.ADIANTAMENTO)
                .toList();

        return adiantamentos.stream().map(s -> {
            VisaoAdiantamentoDTO dto = new VisaoAdiantamentoDTO();

            dto.setSolicitacaoId(s.getId());
            dto.setStatusFaturamento(s.getStatus());

            dto.setDataSolicitacao(s.getDataSolicitacao());

            try {

            } catch (Exception e) {}

            return dto;
        }).collect(Collectors.toList());
    }

    // --- AÇÕES ---

    @Transactional
    public SolicitacaoFaturamento solicitarIdFaturamento(Long osLpuDetalheId, Long solicitanteId) {
        return criarSolicitacao(osLpuDetalheId, solicitanteId, TipoFaturamento.REGULAR);
    }

    @Transactional
    public SolicitacaoFaturamento solicitarAdiantamento(Long osLpuDetalheId, Long solicitanteId) {
        return criarSolicitacao(osLpuDetalheId, solicitanteId, TipoFaturamento.ADIANTAMENTO);
    }

    private SolicitacaoFaturamento criarSolicitacao(Long osLpuDetalheId, Long solicitanteId, TipoFaturamento tipo) {
        if (repo.existsByOsLpuDetalheId(osLpuDetalheId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe solicitação para este item.");
        }
        OsLpuDetalheDTO detalhe = monolitoClient.getDetalhe(osLpuDetalheId);
        SolicitacaoFaturamento sol = new SolicitacaoFaturamento();
        sol.setOsLpuDetalheId(osLpuDetalheId);
        sol.setSolicitanteId(solicitanteId);
        sol.setSegmentoId(detalhe.getSegmentoId());
        sol.setStatus(StatusFaturamento.PENDENTE_ASSISTANT);
        sol.setTipo(tipo);
        // Sugestão: Adicionar campos numeroOs e descricaoItem na entidade SolicitacaoFaturamento para evitar calls extras
        return repo.save(sol);
    }

    @Transactional
    public SolicitacaoFaturamento alterarStatus(Long solicitacaoId, AcaoFaturamentoDTO acao) {
        SolicitacaoFaturamento solicitacao = repo.findById(solicitacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada."));

        // Validações de recusa
        if (acao.getNovoStatus() == StatusFaturamento.ID_RECUSADO && (acao.getMotivo() == null || acao.getMotivo().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Motivo é obrigatório para recusar.");
        }

        // Atualiza microsserviço
        solicitacao.setStatus(acao.getNovoStatus());
        solicitacao.setResponsavelId(acao.getUsuarioId());
        solicitacao.setDataUltimaAcao(LocalDateTime.now());

        if (acao.getNovoStatus() == StatusFaturamento.ID_RECUSADO) {
            solicitacao.setObservacao(acao.getMotivo());
        }

        // >>> INTEGRAÇÃO: Atualizar Legado (Coluna Faturamento) <<<
        String statusLegado = null;

        switch (acao.getNovoStatus()) {
            case ID_SOLICITADO:
                statusLegado = "ID SOLICITADO";
                break;
            case ID_RECEBIDO:
                statusLegado = "ID RECEBIDO";
                break;
            case FATURADO:
                // Regra: Se for Adiantamento, escreve diferente
                if (solicitacao.getTipo() == TipoFaturamento.ADIANTAMENTO) {
                    statusLegado = "FATURADO EM ADIANTAMENTO";
                } else {
                    statusLegado = "FATURADO";
                }
                break;
            // PENDENTE e RECUSADO não alteram o legado ou limpam?
            // Geralmente mantemos o último status válido ou tratamos recusa separadamente.
            default:
                break;
        }

        if (statusLegado != null) {
            try {
                monolitoClient.atualizarStatusLancamento(solicitacao.getOsLpuDetalheId(), statusLegado);
            } catch (Exception e) {
                // Logar erro, mas cuidado com rollback se o legado estiver fora
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao sincronizar com Monólito: " + e.getMessage());
            }
        }

        return repo.save(solicitacao);
    }

    // --- DASHBOARD ---
    @Transactional(readOnly = true)
    public DashboardFaturamentoDTO getDashboardFaturamento(Long usuarioId) {
        UsuarioDTO usuario = monolitoClient.getUsuario(usuarioId);
        boolean isCoordinator = "COORDINATOR".equals(usuario.getRole());

        long pendenteSolicitacao = 0;
        if (isCoordinator) {
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
        long adiantamentos = baseQuery.stream().filter(s -> s.getTipo() == TipoFaturamento.ADIANTAMENTO && s.getStatus() != StatusFaturamento.FATURADO).count();

        LocalDateTime trintaDiasAtras = LocalDateTime.now().minusDays(30);
        long faturadoMes = baseQuery.stream()
                .filter(s -> s.getStatus() == StatusFaturamento.FATURADO && s.getDataUltimaAcao().isAfter(trintaDiasAtras))
                .count();

        return new DashboardFaturamentoDTO(pendenteSolicitacao, pendenteFila, idsRecusados, adiantamentos, faturadoMes);
    }

    // Helpers
    private SolicitacaoFaturamentoDTO toDTO(SolicitacaoFaturamento entity) {
        SolicitacaoFaturamentoDTO dto = new SolicitacaoFaturamentoDTO();
        dto.setId(entity.getId());
        dto.setOsLpuDetalheId(entity.getOsLpuDetalheId());
        // Aqui o ideal seria buscar os dados da OS no monólito para preencher o DTO,
        // mas faremos isso de forma leve ou o frontend busca se faltar.
        // Como o endpoint getFilaAssistant precisa mostrar dados, o ideal é enriquecer esse DTO.
        // Por hora, manteremos simples.
        dto.setStatus(entity.getStatus());
        dto.setTipo(entity.getTipo());
        dto.setDataSolicitacao(entity.getDataSolicitacao());
        dto.setDataUltimaAcao(entity.getDataUltimaAcao());
        dto.setObservacao(entity.getObservacao());

        // Tentativa de enriquecimento rápido (pode ser lento se a lista for grande, ideal é cache ou salvar na tabela)
        try {
            OsLpuDetalheDTO detalhes = monolitoClient.getDetalhe(entity.getOsLpuDetalheId());
            dto.setNumeroOs(detalhes.getNumeroOs());
            dto.setDescricaoItem(detalhes.getDescricao());
            dto.setValor(detalhes.getPrecoTotalInprout());
        } catch (Exception e) {
            dto.setDescricaoItem("Erro ao carregar detalhes");
        }

        return dto;
    }

    private FilaCoordenadorDTO toFilaDTO(ItemCandidatoDTO item) {
        FilaCoordenadorDTO dto = new FilaCoordenadorDTO();
        dto.setOsLpuDetalheId(item.getOsLpuDetalheId());
        dto.setNumeroOs(item.getNumeroOs());
        dto.setDescricaoItem(item.getDescricaoItem());
        dto.setSegmento(item.getSegmento());
        return dto;
    }
}