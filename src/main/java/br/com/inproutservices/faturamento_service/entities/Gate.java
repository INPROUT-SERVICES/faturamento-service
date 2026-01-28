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

    // Relacionamento local (Agora funciona pois ambos estão no mesmo serviço)
    @OneToOne
    @JoinColumn(name = "solicitacao_faturamento_id", nullable = false)
    private SolicitacaoFaturamento solicitacao;

    // Referência remota ao Monólito (Mudamos de Usuario para Long)
    @Column(name = "usuario_responsavel_id")
    private Long usuarioResponsavelId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoAprovacao status;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    private LocalDateTime dataAcao;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.dataAcao == null) this.dataAcao = LocalDateTime.now();
    }
}