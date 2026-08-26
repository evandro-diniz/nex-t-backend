package com.nex_t.controller;
import com.nex_t.config.AuthenticationUsuario;
import com.nex_t.dto.TarefaRequest;
import com.nex_t.dto.TarefaResponse;
import com.nex_t.entity.Usuario;
import com.nex_t.entity.enums.StatusTarefa;
import com.nex_t.service.TarefaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tarefas")
@RequiredArgsConstructor
public class TarefaController {

    private final TarefaService tarefaService;

    @GetMapping
    public List<TarefaResponse> listar(
            @AuthenticationUsuario Usuario usuario,
            @RequestParam(required = false) LocalDate dia
    ) {
        return tarefaService.listarDoDia(usuario, dia != null ? dia : LocalDate.now());
    }

    @PostMapping
    public ResponseEntity<TarefaResponse> criar(
            @AuthenticationUsuario Usuario usuario,
            @Valid @RequestBody TarefaRequest request
    ) {
        return ResponseEntity.ok(tarefaService.criar(usuario, request));
    }

    @PatchMapping("/{id}/status")
    public TarefaResponse moverStatus(@PathVariable UUID id, @RequestParam StatusTarefa novoStatus) {
        return tarefaService.moverStatus(id, novoStatus);
    }

    @PostMapping("/iniciar")
    public TarefaResponse iniciar(@AuthenticationUsuario Usuario usuario) {
        return tarefaService.iniciarProxima(usuario);
    }

    @PostMapping("/{id}/adiar-hoje")
    public TarefaResponse adiarHoje(@PathVariable UUID id) {
        return tarefaService.adiarHoje(id);
    }

    @PostMapping("/{id}/adiar-amanha")
    public TarefaResponse adiarAmanha(@PathVariable UUID id) {
        return tarefaService.adiarAmanha(id);
    }

    @PostMapping("/{id}/concluir")
    public TarefaResponse concluir(@PathVariable UUID id) {
        return tarefaService.concluir(id);
    }

    @PostMapping("/{id}/reiniciar")
    public TarefaResponse reiniciar(@PathVariable UUID id) {
        return tarefaService.reiniciar(id);
    }

    @GetMapping("/historico")
    public List<TarefaResponse> historico(
            @AuthenticationUsuario Usuario usuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate
    ) {
        return tarefaService.listarHistorico(usuario, de, ate);
    }
}
