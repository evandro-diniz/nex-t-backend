package com.nex_t.repository;

import com.nex_t.entity.Tarefa;
import com.nex_t.entity.Usuario;
import com.nex_t.entity.enums.StatusTarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TarefaRepository extends JpaRepository<Tarefa, UUID> {

    List<Tarefa> findByUsuarioAndDataReferencia(Usuario usuario, LocalDate dataReferencia);

    List<Tarefa> findByUsuarioAndStatus(Usuario usuario, StatusTarefa status);

    // usado pelo job de rollover: tarefas de dias anteriores que não chegaram a FEITO
    List<Tarefa> findByStatusNotAndDataReferenciaBefore(StatusTarefa status, LocalDate data);

    // adicionado posteriormente

    List<Tarefa> findByUsuarioAndStatusNotAndDataReferenciaBefore(Usuario usuario, StatusTarefa status, LocalDate data);

    List<Tarefa> findByUsuarioOrderByDataReferenciaAscCriadaEmAsc(Usuario usuario);

    List<Tarefa> findByUsuarioAndDataReferenciaBetweenOrderByDataReferenciaAscCriadaEmAsc(
            Usuario usuario, LocalDate de, LocalDate ate);
}
