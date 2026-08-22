package com.nex_t.dto;

import com.nex_t.entity.enums.Categoria;
import com.nex_t.entity.enums.Impacto;
import com.nex_t.entity.enums.Prazo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TarefaRequest {

    @NotBlank
    private String titulo;

    @NotNull
    private Categoria categoria;

    @NotNull
    private Prazo prazo;

    private boolean urgente;

    @NotNull
    private Impacto impacto;

    private String nota;
}
