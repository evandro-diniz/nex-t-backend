package com.nex_t.repository;

import com.nex_t.entity.SessaoPomodoro;
import com.nex_t.entity.enums.StatusSessao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SessaoPomodoroRepository extends JpaRepository<SessaoPomodoro, UUID> {
    List<SessaoPomodoro> findByTarefaIdAndStatus(UUID tarefaId, StatusSessao status);
}
