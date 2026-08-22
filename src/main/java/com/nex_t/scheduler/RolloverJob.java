package com.nex_t.scheduler;

import com.nex_t.entity.EventoTarefa;
import com.nex_t.entity.Tarefa;
import com.nex_t.entity.enums.StatusTarefa;
import com.nex_t.entity.enums.TipoEvento;
import com.nex_t.repository.EventoTarefaRepository;
import com.nex_t.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Roda diariamente (horário configurável em app.rollover.cron).
 * Toda tarefa que não chegou a FEITO no dia anterior "rola" para hoje,
 * mantendo a prioridade original. Se estava marcada para amanhã, volta a
 * ficar elegível para seleção automática (paraAmanha = false).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RolloverJob {

    private final TarefaRepository tarefaRepository;
    private final EventoTarefaRepository eventoTarefaRepository;

    @Scheduled(cron = "${app.rollover.cron}")
    public void rolarTarefasNaoConcluidas() {
        LocalDate hoje = LocalDate.now();
        List<Tarefa> pendentes = tarefaRepository.findByStatusNotAndDataReferenciaBefore(StatusTarefa.FEITO, hoje);

        pendentes.forEach(tarefa -> {
            tarefa.setDataReferencia(hoje);
            tarefa.setParaAmanha(false);
            eventoTarefaRepository.save(EventoTarefa.builder().tarefa(tarefa).tipo(TipoEvento.ROLADA).build());
        });

        tarefaRepository.saveAll(pendentes);
        log.info("Rollover: {} tarefa(s) movida(s) para o painel de hoje ({}).", pendentes.size(), hoje);
    }
}
