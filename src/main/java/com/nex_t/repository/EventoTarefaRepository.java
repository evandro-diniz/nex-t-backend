package com.nex_t.repository;

import com.nex_t.entity.EventoTarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventoTarefaRepository extends JpaRepository<EventoTarefa, UUID> {
}
