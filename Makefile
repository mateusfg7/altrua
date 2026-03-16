# Cores
RESET  = \033[0m
BOLD   = \033[1m
RED    = \033[31m
GREEN  = \033[32m
YELLOW = \033[33m
BLUE   = \033[34m
CYAN   = \033[36m
WHITE  = \033[97m
DIM    = \033[2m

-include .env
export

.PHONY: help run build test dev clean docker-up docker-down logs setup migration \
        db-clean db-reset db-migrate db-migrate-info db-migrate-repair db-migrate-validate

## Exibe esta mensagem de ajuda
help:
	@printf "$(RESET)\n"
	@printf "$(BOLD)$(CYAN)╔══════════════════════════════════════════╗$(RESET)\n"
	@printf "$(BOLD)$(CYAN)║        🍃  Spring App — Comandos          ║$(RESET)\n"
	@printf "$(BOLD)$(CYAN)╚══════════════════════════════════════════╝$(RESET)\n"
	@printf "\n"
	@printf "$(BOLD)$(WHITE)  DESENVOLVIMENTO$(RESET)\n"
	@printf "  $(GREEN)make setup$(RESET)              $(DIM)Configura o projeto pela primeira vez$(RESET)\n"
	@printf "  $(GREEN)make dev$(RESET)                $(DIM)Sobe o banco e inicia a aplicação$(RESET)\n"
	@printf "  $(GREEN)make run$(RESET)                $(DIM)Inicia apenas a aplicação$(RESET)\n"
	@printf "\n"
	@printf "$(BOLD)$(WHITE)  BUILD & TESTES$(RESET)\n"
	@printf "  $(GREEN)make build$(RESET)              $(DIM)Compila o projeto (sem rodar testes)$(RESET)\n"
	@printf "  $(GREEN)make test$(RESET)               $(DIM)Executa os testes automatizados$(RESET)\n"
	@printf "  $(GREEN)make clean$(RESET)              $(DIM)Remove arquivos de build gerados$(RESET)\n"
	@printf "\n"
	@printf "$(BOLD)$(WHITE)  BANCO DE DADOS$(RESET)\n"
	@printf "  $(GREEN)make migration$(RESET)          $(DIM)Cria um novo arquivo de migration Flyway$(RESET)\n"
	@printf "  $(GREEN)make db-migrate$(RESET)         $(DIM)Executa as migrations pendentes via Flyway$(RESET)\n"
	@printf "  $(GREEN)make db-migrate-info$(RESET)    $(DIM)Exibe o status de todas as migrations$(RESET)\n"
	@printf "  $(GREEN)make db-migrate-validate$(RESET)$(DIM)Valida as migrations aplicadas$(RESET)\n"
	@printf "  $(GREEN)make db-migrate-repair$(RESET)  $(DIM)Repara o histórico de migrations com falha$(RESET)\n"
	@printf "  $(GREEN)make db-clean$(RESET)           $(DIM)Remove volumes do banco (dados apagados!)$(RESET)\n"
	@printf "  $(GREEN)make db-reset$(RESET)           $(DIM)Derruba tudo, recria e roda as migrations$(RESET)\n"
	@printf "\n"
	@printf "$(BOLD)$(WHITE)  DOCKER$(RESET)\n"
	@printf "  $(GREEN)make docker-up$(RESET)          $(DIM)Sobe os containers em background$(RESET)\n"
	@printf "  $(GREEN)make docker-down$(RESET)        $(DIM)Para e remove os containers$(RESET)\n"
	@printf "  $(GREEN)make logs$(RESET)               $(DIM)Exibe os logs do PostgreSQL em tempo real$(RESET)\n"
	@printf "\n"

## Configura o projeto: cria o .env a partir do .env.example
setup:
	@printf "$(CYAN)⚙️  Configurando o projeto...$(RESET)\n"
	@if [ -f .env ]; then \
		printf "$(YELLOW)⚠️  Arquivo .env já existe — nenhuma alteração feita.$(RESET)\n"; \
	else \
		cp .env.example .env; \
		printf "$(GREEN)✅ .env criado a partir do .env.example$(RESET)\n"; \
		printf "$(DIM)   Preencha as credenciais antes de rodar o projeto.$(RESET)\n"; \
	fi

## Inicia apenas a aplicação Spring
run:
	@printf "$(CYAN)🚀 Iniciando a aplicação...$(RESET)\n"
	@./mvnw spring-boot:run

## Compila o projeto sem rodar os testes
build:
	@printf "$(CYAN)🔨 Compilando o projeto...$(RESET)\n"
	@./mvnw clean package -DskipTests
	@printf "$(GREEN)✅ Build concluído.$(RESET)\n"

## Executa os testes automatizados
test:
	@printf "$(CYAN)🧪 Executando testes...$(RESET)\n"
	@./mvnw test

## Sobe o banco e inicia a aplicação
dev: docker-up run

## Remove os arquivos de build
clean:
	@printf "$(YELLOW)🧹 Limpando arquivos de build...$(RESET)\n"
	@./mvnw clean
	@printf "$(GREEN)✅ Limpeza concluída.$(RESET)\n"

## Sobe os containers Docker em background
docker-up:
	@printf "$(CYAN)🐳 Subindo containers...$(RESET)\n"
	@docker compose up -d
	@printf "$(GREEN)✅ Containers no ar.$(RESET)\n"

## Para e remove os containers Docker
docker-down:
	@printf "$(YELLOW)🛑 Parando containers...$(RESET)\n"
	@docker compose down
	@printf "$(GREEN)✅ Containers encerrados.$(RESET)\n"

## Exibe os logs do PostgreSQL em tempo real
logs:
	@printf "$(CYAN)📋 Logs do PostgreSQL (Ctrl+C para sair)...$(RESET)\n"
	@docker compose logs -f postgres

## Cria um novo arquivo de migration Flyway
migration:
	@printf "$(CYAN)📝 Nova migration$(RESET)\n"
	@printf "$(BOLD)$(WHITE)  Nome da migration: $(RESET)"; \
	read NAME; \
	TIMESTAMP=$$(date +"%Y%m%d%H%M%S"); \
	SNAKE=$$(echo "$$NAME" | tr '[:upper:]' '[:lower:]' | sed 's/[[:space:]]\+/_/g' | sed 's/[^a-z0-9_]//g'); \
	if [ -z "$$SNAKE" ]; then \
		printf "$(RED)❌ Nome de migration inválido. Use pelo menos um caractere alfanumérico.$(RESET)\n"; \
		exit 1; \
	fi; \
	MIGRATION_DIR="src/main/resources/db/migration"; \
	FILENAME="V$${TIMESTAMP}__$${SNAKE}.sql"; \
	mkdir -p "$$MIGRATION_DIR"; \
	touch "$$MIGRATION_DIR/$$FILENAME"; \
	printf "$(GREEN)✅ Migration criada: $(RESET)$(BOLD)$$MIGRATION_DIR/$$FILENAME$(RESET)\n"

# ─────────────────────────────────────────────
#  BANCO DE DADOS — Flyway & Cleanup
# ─────────────────────────────────────────────

## Executa as migrations pendentes via Flyway (Maven)
db-migrate:
	@printf "$(CYAN)🛠️  Rodando migrations Flyway...$(RESET)\n"
	@./mvnw flyway:migrate -Dflyway.url=jdbc:postgresql://$${DB_HOST:-localhost}:$${DB_PORT:-5432}/$${DB_NAME} \
	                       -Dflyway.user=$${DB_USER} \
	                       -Dflyway.password=$${DB_PASSWORD}
	@printf "$(GREEN)✅ Migrations aplicadas com sucesso.$(RESET)\n"

## Exibe o status de todas as migrations (aplicadas, pendentes, com falha)
db-migrate-info:
	@printf "$(CYAN)📊 Status das migrations Flyway...$(RESET)\n"
	@./mvnw flyway:info -Dflyway.url=jdbc:postgresql://$${DB_HOST:-localhost}:$${DB_PORT:-5432}/$${DB_NAME} \
	                    -Dflyway.user=$${DB_USER} \
	                    -Dflyway.password=$${DB_PASSWORD}

## Valida se as migrations aplicadas correspondem às locais
db-migrate-validate:
	@printf "$(CYAN)🔍 Validando migrations Flyway...$(RESET)\n"
	@./mvnw flyway:validate -Dflyway.url=jdbc:postgresql://$${DB_HOST:-localhost}:$${DB_PORT:-5432}/$${DB_NAME} \
	                        -Dflyway.user=$${DB_USER} \
	                        -Dflyway.password=$${DB_PASSWORD}
	@printf "$(GREEN)✅ Validação concluída.$(RESET)\n"

## Repara o histórico de migrations com falha (checksums e registros quebrados)
db-migrate-repair:
	@printf "$(YELLOW)🔧 Reparando histórico de migrations Flyway...$(RESET)\n"
	@./mvnw flyway:repair -Dflyway.url=jdbc:postgresql://$${DB_HOST:-localhost}:$${DB_PORT:-5432}/$${DB_NAME} \
	                      -Dflyway.user=$${DB_USER} \
	                      -Dflyway.password=$${DB_PASSWORD}
	@printf "$(GREEN)✅ Reparo concluído.$(RESET)\n"

## Remove containers + volumes do banco (⚠️  TODOS OS DADOS SERÃO APAGADOS)
db-clean:
	@printf "$(RED)$(BOLD)⚠️  ATENÇÃO: todos os dados do banco serão apagados!$(RESET)\n"
	@printf "$(YELLOW)   Pressione ENTER para continuar ou Ctrl+C para cancelar...$(RESET)"; \
	read _CONFIRM
	@printf "$(YELLOW)🗑️  Removendo containers e volumes do PostgreSQL...$(RESET)\n"
	@docker compose down -v --remove-orphans
	@printf "$(GREEN)✅ Containers e volumes removidos.$(RESET)\n"

## Derruba tudo, recria o banco e executa todas as migrations do zero
db-reset: db-clean docker-up db-migrate
	@printf "$(GREEN)$(BOLD)✅ Banco resetado e migrations aplicadas com sucesso!$(RESET)\n"