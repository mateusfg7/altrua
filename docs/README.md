# 🤝 Altrua

> Plataforma de cadastro de ONGs, eventos e voluntários.

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.5-green?style=flat-square&logo=springboot)
![React](https://img.shields.io/badge/React-19-61DAFB?style=flat-square&logo=react)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?style=flat-square&logo=docker)
![License](https://img.shields.io/badge/license-GPL--3.0-lightgrey?style=flat-square)

---

## 📋 Sobre o projeto

O **Altrua** é uma plataforma fullstack que conecta Organizações Não Governamentais (ONGs) a voluntários. As ONGs podem se cadastrar e publicar eventos, enquanto usuários podem se inscrever como voluntários.

O projeto é composto por:

- **Backend**: API REST com Java 21 e Spring Boot
- **Frontend**: Interface web com React 19 e TanStack Start

Projeto desenvolvido como **Projeto Integrador** do curso — utilizando uma stack moderna com Java 21, Spring Boot, PostgreSQL, React 19 e Docker.

---

## ✨ Funcionalidades

- ✅ Cadastro e autenticação de usuários (JWT com refresh token)
- ✅ Cadastro e listagem de ONGs com filtros e paginação
- ✅ Criação e encerramento de eventos por ONGs
- ✅ Inscrição e cancelamento de voluntários em eventos
- ✅ API REST documentada (Scalar UI)

---

## 🛠️ Tecnologias

### Backend

| Tecnologia        | Versão | Uso                          |
| ----------------- | ------ | ---------------------------- |
| Java              | 21     | Linguagem principal          |
| Spring Boot       | 4.0.5  | Framework web                |
| Spring Data JPA   | —      | ORM e acesso ao banco        |
| Spring Security   | —      | Autenticação e autorização   |
| Spring Validation | —      | Validação de dados           |
| Flyway            | —      | Migrations do banco          |
| JJWT              | 0.13.0 | Geração e validação de JWT   |
| Springdoc OpenAPI | 3.0.3  | Documentação da API (Scalar) |
| Lombok            | —      | Redução de boilerplate       |
| PostgreSQL        | 16     | Banco de dados relacional    |
| Docker            | —      | Containerização              |
| Docker Compose    | —      | Orquestração local           |
| Maven             | —      | Gerenciador de dependências  |

### Frontend

| Tecnologia      | Versão | Uso                           |
| --------------- | ------ | ----------------------------- |
| React           | 19     | Biblioteca de UI              |
| TanStack Start  | latest | Framework SSR/SPA             |
| TanStack Router | latest | Roteamento type-safe          |
| TanStack Query  | latest | Gerenciamento de estado async |
| TailwindCSS     | 4      | Estilização                   |
| Vite            | 8      | Bundler e dev server          |
| TypeScript      | 6      | Tipagem estática              |
| Zod             | 4      | Validação de schemas          |
| Axios           | 1      | Cliente HTTP                  |
| Vitest          | 4      | Testes unitários              |

---

## 📁 Estrutura do projeto

```
altrua/
├── backend/
│   ├── src/main/java/com/techfun/altrua/
│   │   ├── core/        # Exceções de domínio e utilitários compartilhados
│   │   ├── features/    # Módulos de negócio (auth, event, ong, user)
│   │   │                # Cada módulo segue a estrutura: api/ domain/ repository/ service/
│   │   └── infra/       # Configurações globais e camada de segurança (JWT)
│   ├── src/main/resources/
│   │   └── db/migration/  # Scripts Flyway versionados
│   ├── docker-compose.yaml
│   ├── Makefile
│   └── pom.xml
└── web/
    └── src/
        ├── features/    # Módulos por domínio (ngos, ...)
        ├── routes/      # Páginas (file-based routing)
        └── shared/      # Componentes, lib e tipos compartilhados
```

---

## 🚀 Como executar

### Pré-requisitos

- [Docker](https://www.docker.com/) e [Docker Compose](https://docs.docker.com/compose/)
- [Java 21](https://adoptium.net/)
- [Node.js](https://nodejs.org/) e [pnpm](https://pnpm.io/)
- [Make](https://www.gnu.org/software/make/)

### 1. Clone o repositório

```bash
git clone https://github.com/mateusfg7/altrua.git
cd altrua
```

### 2. Backend

```bash
cd backend

# Configura o .env a partir do .env.example
make setup

# Sobe o banco e inicia a aplicação
make dev
```

A API estará disponível em `http://localhost:8080`.  
A documentação interativa estará em `http://localhost:8080/docs`.

### 3. Frontend

```bash
cd web
pnpm install
pnpm dev
```

O frontend estará disponível em `http://localhost:3000`.

---

## ⚙️ Variáveis de ambiente

Copie o `.env.example` e preencha os valores:

```bash
cp backend/.env.example backend/.env
```

| Variável                       | Descrição                                        | Padrão                  |
| ------------------------------ | ------------------------------------------------ | ----------------------- |
| `DB_NAME`                      | Nome do banco de dados                           | —                       |
| `DB_USER`                      | Usuário do PostgreSQL                            | —                       |
| `DB_PASSWORD`                  | Senha do PostgreSQL                              | —                       |
| `JWT_SECRET`                   | Chave secreta para assinar tokens JWT (Base64)   | —                       |
| `JWT_EXPIRATION`               | Expiração do access token em ms                  | `1800000` (30min)       |
| `JWT_REFRESH_TOKEN_EXPIRATION` | Expiração do refresh token em ms                 | `2592000000` (30d)      |
| `CORS_ORIGINS`                 | Origens permitidas para CORS (separadas por `,`) | `http://localhost:3000` |

> ⚠️ **Nunca commite o `.env` com credenciais reais.** Ele já está no `.gitignore`.

Para gerar um `JWT_SECRET` seguro:

```bash
openssl rand -base64 64
```

---

## 🧰 Comandos disponíveis (backend)

```bash
make help                 # Lista todos os comandos disponíveis
make setup                # Configura o projeto (cria o .env a partir do .env.example)
make dev                  # Sobe o banco e inicia a aplicação
make run                  # Inicia apenas a aplicação
make build                # Compila o projeto (sem rodar os testes)
make test                 # Executa os testes automatizados
make clean                # Remove arquivos de build
make migration            # Cria um novo arquivo de migration Flyway
make db-migrate           # Executa as migrations pendentes
make db-migrate-info      # Exibe o status de todas as migrations
make db-migrate-validate  # Valida as migrations aplicadas
make db-migrate-repair    # Repara o histórico de migrations com falha
make db-clean             # Remove volumes do banco (dados apagados!)
make db-reset             # Derruba tudo, recria e roda as migrations
make docker-up            # Sobe os containers em background
make docker-down          # Para e remove os containers
make logs                 # Exibe os logs do PostgreSQL em tempo real
```

---

## 🔌 API REST

A documentação interativa completa dos endpoints está disponível via **Scalar UI** após subir o backend:

```
http://localhost:8080/docs
```

O JSON da especificação OpenAPI está em:

```
http://localhost:8080/api-docs
```

---

## 👥 Colaboradores

| Nome                            | GitHub                                         | RA FAPAM |
| ------------------------------- | ---------------------------------------------- | -------- |
| Mateus Felipe Gonçalves         | [@mateusfg7](https://github.com/mateusfg7)     | 16349    |
| Gabriel Henrique Sousa Mendonça | [@gabriel-mkv](https://github.com/gabriel-mkv) | 16359    |

---

## 📄 Licença

Este projeto está sob a licença [GPL-3.0](LICENSE).
