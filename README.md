# nex-t — backend

Esqueleto do backend em Java 21 + Spring Boot 3 para o **nex-t**
(Scrum + Kanban + Pomodoro).

## Como rodar localmente

1. Tenha um PostgreSQL rodando (local ou já no Neon/Supabase).
2. Configure as variáveis de ambiente (ou um `.env` / `application-local.yml`):

   ```
   DATABASE_URL=jdbc:postgresql://localhost:5432/nex_t
   DATABASE_USERNAME=postgres
   DATABASE_PASSWORD=postgres
   GOOGLE_CLIENT_ID=...        # do Google Cloud Console
   GOOGLE_CLIENT_SECRET=...
   JWT_SECRET=uma-string-longa-e-aleatoria
   FRONTEND_URL=http://localhost:5173
   ```

3. Rode:
   ```
   ./mvnw spring-boot:run
   ```

## O que já está pronto

- Entidades: `Usuario`, `Tarefa`, `SessaoPomodoro`, `EventoTarefa` (auditoria)
- Regra de priorização (`PrioridadeService`) — a mesma fórmula que já validamos:
  score = pontos(prazo) + pontos(urgência) + pontos(impacto)
- Endpoints REST em `/api/tarefas` e `/api/sessoes` (ver arquivo de arquitetura)
- Login com Google (OAuth2) gerando um JWT que o React usa nas chamadas seguintes
- Job diário de rollover das tarefas não concluídas
- CORS configurado via `FRONTEND_URL`

## O que falta (próximos passos)

- Endpoints de relatório (`/api/relatorios`) com as métricas já definidas
- Envio de notificações Web Push (fim de Pomodoro, sugestão de pausa)
- Testes automatizados
- Deploy no Render + banco no Neon (ver documento de arquitetura)

## Estrutura de pacotes

```
com.nex_t
 ├── config/       Segurança, OAuth2, JWT, CORS
 ├── entity/       Entidades JPA + enums do domínio
 ├── repository/   Spring Data JPA
 ├── service/      Regras de negócio (priorização, tarefas)
 ├── controller/   Endpoints REST
 ├── scheduler/    Job de rollover diário
 └── dto/          Objetos de entrada/saída da API
```
