package com.nex_t.service;

import com.nex_t.entity.Tarefa;
import com.nex_t.entity.enums.Categoria;
import com.nex_t.entity.enums.Impacto;
import com.nex_t.entity.enums.Prazo;
import com.nex_t.entity.enums.StatusTarefa;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PrioridadeServiceTest {

    private final PrioridadeService service = new PrioridadeService();

    private Tarefa criar(Prazo prazo, boolean urgente, Impacto impacto, LocalDateTime criadaEm) {
        return Tarefa.builder()
                .titulo("tarefa de teste")
                .categoria(Categoria.TRABALHO)
                .prazo(prazo)
                .urgente(urgente)
                .impacto(impacto)
                .status(StatusTarefa.A_FAZER)
                .dataReferencia(LocalDate.now())
                .criadaEm(criadaEm)
                .build();
    }

    @Test
    void tarefaUrgenteDeHojeComAltoImpactoTemScoreMaximo() {
        Tarefa tarefa = criar(Prazo.HOJE, true, Impacto.ALTO, LocalDateTime.now());
        // 3 (hoje) + 2 (urgente) + 3 (alto) = 8
        assertThat(service.calcularScore(tarefa)).isEqualTo(8);
    }

    @Test
    void tarefaDeSemanaSemUrgenciaEBaixoImpactoTemScoreMinimo() {
        Tarefa tarefa = criar(Prazo.SEMANA_MAIS, false, Impacto.BAIXO, LocalDateTime.now());
        // 1 (semana+) + 0 (não urgente) + 1 (baixo) = 2
        assertThat(service.calcularScore(tarefa)).isEqualTo(2);
    }

    @Test
    void ordenaPorScoreDoMaiorParaOMenor() {
        Tarefa alta = criar(Prazo.HOJE, true, Impacto.ALTO, LocalDateTime.now());       // 8
        Tarefa media = criar(Prazo.HOJE, false, Impacto.MEDIO, LocalDateTime.now());    // 5
        Tarefa baixa = criar(Prazo.SEMANA_MAIS, false, Impacto.BAIXO, LocalDateTime.now()); // 2

        List<Tarefa> ordenadas = service.ordenarPorPrioridade(List.of(media, baixa, alta));

        assertThat(ordenadas).containsExactly(alta, media, baixa);
    }

    @Test
    void emCasoDeEmpateTarefaMaisAntigaVence() {
        LocalDateTime maisAntiga = LocalDateTime.now().minusDays(3);
        LocalDateTime maisRecente = LocalDateTime.now();

        Tarefa antiga = criar(Prazo.HOJE, false, Impacto.MEDIO, maisAntiga);   // score 5
        Tarefa recente = criar(Prazo.HOJE, false, Impacto.MEDIO, maisRecente); // score 5, empatada

        Optional<Tarefa> proxima = service.proximaTarefa(List.of(recente, antiga));

        assertThat(proxima).contains(antiga);
    }
}
