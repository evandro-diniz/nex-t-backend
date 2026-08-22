package com.nex_t.entity;

import com.nex_t.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tarefas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarefa {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prazo prazo;

    @Column(nullable = false)
    private boolean urgente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Impacto impacto;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private StatusTarefa status = StatusTarefa.A_FAZER;

    @Builder.Default
    @Column(name = "para_amanha", nullable = false)
    private boolean paraAmanha = false;

    @Column(length = 1000)
    private String nota;

    @Column(name = "data_referencia", nullable = false)
    private LocalDate dataReferencia;

    @Builder.Default
    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm = LocalDateTime.now();

    @Column(name = "atualizada_em")
    private LocalDateTime atualizadaEm;

    @PreUpdate
    public void aoAtualizar() {
        this.atualizadaEm = LocalDateTime.now();
    }
}
