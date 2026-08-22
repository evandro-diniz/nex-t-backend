package com.nex_t.dto;

import com.nex_t.entity.Tarefa;
import com.nex_t.entity.enums.*;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder
public class TarefaResponse {

    UUID id;
    String titulo;
    Categoria categoria;
    Prazo prazo;
    boolean urgente;
    Impacto impacto;
    StatusTarefa status;
    boolean paraAmanha;
    String nota;
    LocalDate dataReferencia;
    int score;

    public static TarefaResponse de(Tarefa t, int score) {
        return TarefaResponse.builder()
                .id(t.getId())
                .titulo(t.getTitulo())
                .categoria(t.getCategoria())
                .prazo(t.getPrazo())
                .urgente(t.isUrgente())
                .impacto(t.getImpacto())
                .status(t.getStatus())
                .paraAmanha(t.isParaAmanha())
                .nota(t.getNota())
                .dataReferencia(t.getDataReferencia())
                .score(score)
                .build();
    }
}
