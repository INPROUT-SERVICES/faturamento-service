package br.com.inproutservices.faturamento_service.repositories;

import br.com.inproutservices.faturamento_service.entities.SolicitacaoFaturamento;
import br.com.inproutservices.faturamento_service.enums.StatusFaturamento;
import br.com.inproutservices.faturamento_service.enums.TipoFaturamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;

public interface SolicitacaoFaturamentoRepository extends JpaRepository<SolicitacaoFaturamento, Long> {

    boolean existsByOsLpuDetalheId(Long osLpuDetalheId);

    List<SolicitacaoFaturamento> findByStatusIn(Collection<StatusFaturamento> statuses);

    // Filtra usando a lista de IDs de segmento que virá do Monólito
    List<SolicitacaoFaturamento> findByStatusInAndSegmentoIdIn(Collection<StatusFaturamento> statuses, Collection<Long> segmentoIds);

    List<SolicitacaoFaturamento> findBySegmentoIdIn(Collection<Long> segmentoIds);

    // Para dashboard
    List<SolicitacaoFaturamento> findByTipoAndStatusNot(TipoFaturamento tipo, StatusFaturamento status);
}