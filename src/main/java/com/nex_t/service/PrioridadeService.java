package com.nex_t.service;

import com.nex_t.entity.Tarefa;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Regra central de priorização do nex-t.
 * Score = pontos(prazo) + pontos(urgencia) + pontos(impacto)
 * Em caso de empate, a tarefa mais antiga no backlog vence (evita esquecimento).
 */
@Service
public class PrioridadeService {

    public int calcularScore(Tarefa tarefa) {
        int prazoPts = tarefa.getPrazo().getPontos();
        int urgenciaPts = tarefa.isUrgente() ? 2 : 0;
        int impactoPts = tarefa.getImpacto().getPontos();
        return prazoPts + urgenciaPts + impactoPts;
    }

    public List<Tarefa> ordenarPorPrioridade(List<Tarefa> tarefas) {
        return tarefas.stream()
                .sorted(
                        Comparator.comparingInt(this::calcularScore).reversed()
                                .thenComparing(Tarefa::getCriadaEm)
                )
                .toList();
    }

    public Optional<Tarefa> proximaTarefa(List<Tarefa> pendentes) {
        return pendentes.stream()
                .filter(t -> !t.isParaAmanha())
                .max(
                        Comparator.comparingInt(this::calcularScore)
                                .thenComparing(Tarefa::getCriadaEm, Comparator.reverseOrder())
                );
    }
}
