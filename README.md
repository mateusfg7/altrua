# 🤝 Altrua

> Plataforma de cadastro de ONGs, eventos, voluntários e doações.

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.3-green?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue?style=flat-square&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?style=flat-square&logo=docker)
![License](https://img.shields.io/badge/license-GPL--3.0-lightgrey?style=flat-square)

---

## 📋 Sobre o projeto

O **Altrua** é uma plataforma REST que conecta Organizações Não Governamentais (ONGs) a voluntários e doadores. As ONGs podem se cadastrar e publicar eventos, enquanto usuários podem se inscrever como voluntários ou acessar informações sobre como contribuir com doações.

Projeto desenvolvido como **Projeto Integrador** do curso — utilizando uma stack moderna com Java 21, Spring Boot, JPA, PostgreSQL e Docker.

---

## ✨ Funcionalidades

- ✅ Cadastro e autenticação de ONGs e usuários
- ✅ Criação e gerenciamento de eventos por ONGs
- ✅ Inscrição de voluntários em eventos
- ✅ Informações sobre doações por evento/ONG
- ✅ Listagem e busca de ONGs e eventos
- ✅ API REST documentada

---

## 🛠️ Tecnologias

| Tecnologia        | Versão | Uso                           |
|-------------------|--------|-------------------------------|
| Java              | 21     | Linguagem principal           |
| Spring Boot       | 4.0.3  | Framework web                 |
| Spring Data JPA   | —      | ORM e acesso ao banco         |
| Spring Security   | —      | Autenticação e autorização    |
| Spring Validation | —      | Validação de dados            |
| Flyway            | —      | Migrations do banco           |
| Springdoc OpenAPI | 3.0.2  | Documentação da API (Swagger) |
| Lombok            | —      | Redução de boilerplate        |
| PostgreSQL        | 16     | Banco de dados relacional     |
| Docker            | —      | Containerização               |
| Docker Compose    | —      | Orquestração local            |
| Maven             | —      | Gerenciador de dependências   |

---

## 📁 Estrutura do projeto

```
altrua/
├── src/
│   ├── main/
│   │   ├── java/com/techfun/altrua/
│   │   │   ├── controller/      # Endpoints REST
│   │   │   ├── service/         # Regras de negócio
│   │   │   ├── repository/      # Acesso ao banco (JPA)
│   │   │   ├── model/           # Entidades JPA
│   │   │   ├── dto/             # Objetos de transferência
│   │   │   └── AltruaApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/    # Scripts Flyway (V1__*.sql, V2__*.sql ...)
│   └── test/
│       └── java/com/techfun/altrua/
│           └── AltruaApplicationTests.java
├── docker-compose.yaml
├── Makefile
├── pom.xml
└── README.md
```

---

## 🚀 Como executar

### Pré-requisitos

- [Docker](https://www.docker.com/) e [Docker Compose](https://docs.docker.com/compose/)
- [Java 21](https://adoptium.net/)
- [Make](https://www.gnu.org/software/make/)

### 1. Clone o repositório

```bash
git clone https://github.com/mateusfg7/altrua.git
cd altrua
```

### 2. Configure o ambiente

```bash
make setup
```

Esse comando cria o arquivo `.env` a partir do `.env.example`. Abra o `.env` e preencha as credenciais antes de continuar.

### 3. Suba tudo

```bash
make dev
```

Esse comando sobe o banco de dados via Docker e inicia a aplicação. As migrations do Flyway são executadas automaticamente na inicialização.

A API estará disponível em: `http://localhost:8080`

---

## ⚙️ Variáveis de ambiente

Copie o `.env.example` e preencha os valores:

```bash
cp .env.example .env
```

| Variável      | Descrição                        | Exemplo                |
|---------------|----------------------------------|------------------------|
| `DB_NAME`     | Nome do banco de dados           | `altrua`               |
| `DB_USER`     | Usuário do PostgreSQL            | `postgres`             |
| `DB_PASSWORD` | Senha do PostgreSQL              | `postgres`             |

O `application.properties` lê essas variáveis automaticamente via `${VAR}`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
```

> ⚠️ **Nunca commite o `.env` com credenciais reais.** Ele já está no `.gitignore`.

---

## 🧰 Comandos disponíveis

```bash
make help        # Lista todos os comandos disponíveis
make setup       # Configura o projeto (cria o .env a partir do .env.example)
make dev         # Sobe o banco e inicia a aplicação
make run         # Inicia apenas a aplicação
make build       # Compila o projeto (sem rodar os testes)
make test        # Executa os testes automatizados
make clean       # Remove arquivos de build
make docker-up   # Sobe os containers em background
make docker-down # Para e remove os containers
make logs        # Exibe os logs do PostgreSQL em tempo real
```

---

## 🐳 Docker Compose

O `docker-compose.yaml` sobe apenas o banco de dados para desenvolvimento local:

```yaml
services:
  postgres:
    image: postgres:16
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
```

---

## 🔌 API REST

A API REST está em desenvolvimento. A documentação dos endpoints será adicionada assim que os controllers forem implementados.

---

## 👥 Colaboradores

| Nome                            | GitHub                                          | RA FAPAM |
|---------------------------------|-------------------------------------------------|----------|
| Mateus Felipe Gonçalves         | [@mateusfg7](https://github.com/mateusfg7)      | 16349    |
| Gabriel Henrique Sousa Mendonça | [@gabriel-mkv](https://github.com/gabriel-mkv)  | 16359    |

---

## 📄 Licença

Este projeto está sob a licença [GPL-3.0](LICENSE).

---

<p align="center">Feito com ❤️ para o Projeto Integrador FAPAM</p>
