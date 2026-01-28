package br.com.inproutservices.faturamento_service.entities;

import br.com.inproutservices.faturamento_service.enums.SituacaoAprovacao;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "gate")
public class Gate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "solicitacao_faturamento_id", nullable = false)
    private SolicitacaoFaturamento solicitacao;

    @Column(name = "usuario_responsavel_id")
    private Long usuarioResponsavelId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoAprovacao status;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    private LocalDateTime dataAcao;

    // --- CAMPOS ADICIONADOS PARA CORRIGIR O ERRO ---
    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;
    // -----------------------------------------------

    @Column(nullable = false)
    private String nome;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.dataAcao == null) this.dataAcao = LocalDateTime.now();
        if (this.dataInicio == null) this.dataInicio = LocalDateTime.now();
        if (this.dataFim == null) this.dataFim = LocalDateTime.now().plusDays(30); // Exemplo
    }
}