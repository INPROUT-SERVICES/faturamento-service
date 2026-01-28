package br.com.inproutservices.faturamento_service.entities;

import br.com.inproutservices.faturamento_service.enums.StatusFaturamento;
import br.com.inproutservices.faturamento_service.enums.TipoFaturamento;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "solicitacao_faturamento")
public class SolicitacaoFaturamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- GUARDAMOS APENAS IDs ---
    @Column(name = "os_lpu_detalhe_id", nullable = false)
    private Long osLpuDetalheId;

    @Column(name = "solicitante_id", nullable = false)
    private Long solicitanteId;

    @Column(name = "responsavel_id")
    private Long responsavelId;

    // Guardamos o ID do segmento aqui para facilitar filtros no dashboard
    @Column(name = "segmento_id")
    private Long segmentoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusFaturamento status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoFaturamento tipo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataSolicitacao;

    private LocalDateTime dataUltimaAcao;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @PrePersist
    protected void onCreate() {
        this.dataSolicitacao = LocalDateTime.now();
        this.dataUltimaAcao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataUltimaAcao = LocalDateTime.now();
    }
}