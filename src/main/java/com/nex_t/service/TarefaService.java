package com.nex_t.service;

import com.nex_t.dto.TarefaRequest;
import com.nex_t.dto.TarefaResponse;
import com.nex_t.entity.EventoTarefa;
import com.nex_t.entity.Tarefa;
import com.nex_t.entity.Usuario;
import com.nex_t.entity.enums.StatusTarefa;
import com.nex_t.entity.enums.TipoEvento;
import com.nex_t.repository.EventoTarefaRepository;
import com.nex_t.repository.TarefaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final EventoTarefaRepository eventoTarefaRepository;
    private final PrioridadeService prioridadeService;

    public List<TarefaResponse> listarDoDia(Usuario usuario, LocalDate dia) {
        if (dia.equals(LocalDate.now())){
            rolarAtrasadasDoUsuario(usuario);
        }
        List<Tarefa> tarefas = tarefaRepository.findByUsuarioAndDataReferencia(usuario, dia);
        return prioridadeService.ordenarPorPrioridade(tarefas).stream()
                .map(t -> TarefaResponse.de(t, prioridadeService.calcularScore(t)))
                .toList();
    }

    private void rolarAtrasadasDoUsuario(Usuario usuario) {
        LocalDate hoje = LocalDate.now();
        List<Tarefa> atrasadas = tarefaRepository
                .findByUsuarioAndStatusNotAndDataReferenciaBefore(usuario, StatusTarefa.FEITO, hoje);
 
        atrasadas.forEach(t -> {
            t.setDataReferencia(hoje);
            t.setParaAmanha(false);
            registrarEvento(t, TipoEvento.ROLADA);
        });
 
        tarefaRepository.saveAll(atrasadas);
    }

    public List<TarefaResponse> listarHistorico(Usuario usuario, LocalDate de, LocalDate ate) {
        List<Tarefa> tarefas = (de != null && ate != null)
                ? tarefaRepository.findByUsuarioAndDataReferenciaBetweenOrderByDataReferenciaAscCriadaEmAsc(usuario, de, ate)
                : tarefaRepository.findByUsuarioOrderByDataReferenciaAscCriadaEmAsc(usuario);
 
        return tarefas.stream()
                .map(t -> TarefaResponse.de(t, prioridadeService.calcularScore(t)))
                .toList();
    }
    
    public TarefaResponse criar(Usuario usuario, TarefaRequest req) {
        Tarefa tarefa = Tarefa.builder()
                .usuario(usuario)
                .titulo(req.getTitulo())
                .categoria(req.getCategoria())
                .prazo(req.getPrazo())
                .urgente(req.isUrgente())
                .impacto(req.getImpacto())
                .nota(req.getNota())
                .status(StatusTarefa.A_FAZER)
                .dataReferencia(LocalDate.now())
                .build();
        tarefaRepository.save(tarefa);
        return TarefaResponse.de(tarefa, prioridadeService.calcularScore(tarefa));
    }

    public TarefaResponse moverStatus(UUID tarefaId, StatusTarefa novoStatus) {
        Tarefa tarefa = buscar(tarefaId);
        tarefa.setStatus(novoStatus);
        tarefaRepository.save(tarefa);
        return TarefaResponse.de(tarefa, prioridadeService.calcularScore(tarefa));
    }

    /** Seleciona automaticamente a próxima tarefa e a move para FAZENDO. */
    public TarefaResponse iniciarProxima(Usuario usuario) {
        List<Tarefa> pendentes = tarefaRepository.findByUsuarioAndStatus(usuario, StatusTarefa.A_FAZER);
        Tarefa proxima = prioridadeService.proximaTarefa(pendentes)
                .orElseThrow(() -> new EntityNotFoundException("Nenhuma tarefa pendente encontrada."));

        proxima.setStatus(StatusTarefa.FAZENDO);
        tarefaRepository.save(proxima);
        registrarEvento(proxima, TipoEvento.INICIADA);

        return TarefaResponse.de(proxima, prioridadeService.calcularScore(proxima));
    }

    /** Mantém a prioridade e sugere a próxima automaticamente (ainda hoje). */
    public TarefaResponse adiarHoje(UUID tarefaId) {
        Tarefa tarefa = buscar(tarefaId);
        tarefa.setStatus(StatusTarefa.A_FAZER);
        tarefaRepository.save(tarefa);
        registrarEvento(tarefa, TipoEvento.ADIADA_HOJE);
        return iniciarProxima(tarefa.getUsuario());
    }

    /** Adia explicitamente para o dia seguinte; permanece visível em A_FAZER. */
    public TarefaResponse adiarAmanha(UUID tarefaId) {
        Tarefa tarefa = buscar(tarefaId);
        tarefa.setStatus(StatusTarefa.A_FAZER);
        tarefa.setParaAmanha(true);
        tarefaRepository.save(tarefa);
        registrarEvento(tarefa, TipoEvento.ADIADA_AMANHA);
        return TarefaResponse.de(tarefa, prioridadeService.calcularScore(tarefa));
    }

    public TarefaResponse concluir(UUID tarefaId) {
        Tarefa tarefa = buscar(tarefaId);
        tarefa.setStatus(StatusTarefa.FEITO);
        tarefaRepository.save(tarefa);
        registrarEvento(tarefa, TipoEvento.CONCLUIDA);
        return TarefaResponse.de(tarefa, prioridadeService.calcularScore(tarefa));
    }

    public TarefaResponse ativarHoje(UUID tarefaId) {
        Tarefa tarefa = buscar(tarefaId);
        tarefa.setStatus(StatusTarefa.A_FAZER);
        tarefa.setDataReferencia(LocalDate.now());
        tarefa.setParaAmanha(false);
        tarefaRepository.save(tarefa);
        registrarEvento(tarefa, TipoEvento.REINICIADA); // reaproveitamos o mesmo tipo de evento
        return TarefaResponse.de(tarefa, prioridadeService.calcularScore(tarefa));
    }

    /** Cria uma cópia da tarefa concluída de volta em A_FAZER (repetir tarefa). */
    public TarefaResponse reiniciar(UUID tarefaId) {
        Tarefa original = buscar(tarefaId);
        Tarefa copia = Tarefa.builder()
                .usuario(original.getUsuario())
                .titulo(original.getTitulo())
                .categoria(original.getCategoria())
                .prazo(original.getPrazo())
                .urgente(original.isUrgente())
                .impacto(original.getImpacto())
                .status(StatusTarefa.A_FAZER)
                .dataReferencia(LocalDate.now())
                .build();
        tarefaRepository.save(copia);
        registrarEvento(copia, TipoEvento.REINICIADA);
        return TarefaResponse.de(copia, prioridadeService.calcularScore(copia));
    }

    public TarefaResponse editar(UUID tarefaId, TarefaRequest req) {
        Tarefa tarefa = buscar(tarefaId);
        tarefa.setTitulo(req.getTitulo());
        tarefa.setCategoria(req.getCategoria());
        tarefa.setPrazo(req.getPrazo());
        tarefa.setUrgente(req.isUrgente());
        tarefa.setImpacto(req.getImpacto());
        tarefa.setNota(req.getNota());
        tarefaRepository.save(tarefa);
        return TarefaResponse.de(tarefa, prioridadeService.calcularScore(tarefa));
    }

    private Tarefa buscar(UUID id) {
        return tarefaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada: " + id));
    }

    private void registrarEvento(Tarefa tarefa, TipoEvento tipo) {
        eventoTarefaRepository.save(EventoTarefa.builder().tarefa(tarefa).tipo(tipo).build());
    }
}
