package com.nex_t.controller;

import com.nex_t.entity.SessaoPomodoro;
import com.nex_t.entity.enums.StatusSessao;
import com.nex_t.repository.SessaoPomodoroRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sessoes")
@RequiredArgsConstructor
public class SessaoController {

    private final SessaoPomodoroRepository sessaoRepository;

    @PostMapping("/{id}/pausar")
    public SessaoPomodoro pausar(@PathVariable UUID id) {
        SessaoPomodoro sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sessão não encontrada: " + id));
        sessao.setStatus(StatusSessao.PAUSADA);
        return sessaoRepository.save(sessao);
    }
}
