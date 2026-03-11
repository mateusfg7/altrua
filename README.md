# 🤝 Altrua

> Plataforma de cadastro de ONGs, eventos, voluntários e doações.

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)
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

| Tecnologia     | Versão  | Uso                          |
|----------------|---------|------------------------------|
| Java           | 21      | Linguagem principal          |
| Spring Boot    | 3.x     | Framework web                |
| Spring Data JPA| -       | ORM e acesso ao banco        |
| PostgreSQL     | 16      | Banco de dados relacional    |
| Docker         | -       | Containerização              |
| Docker Compose | -       | Orquestração local           |
| Maven          | -       | Gerenciador de dependências  |

---

## 🚀 Como executar

### Pré-requisitos

- [Docker](https://www.docker.com/) e [Docker Compose](https://docs.docker.com/compose/) instalados
- [Java 21](https://adoptium.net/) (para rodar sem Docker)
- [Maven](https://maven.apache.org/)

### ▶️ Com Docker (recomendado)
```bash
# Clone o repositório
git clone https://github.com/mateusfg7/altrua.git
cd altrua

# Suba os containers
docker-compose up --build
```

A API estará disponível em: `http://localhost:8080`

---

### ▶️ Sem Docker
```bash
# Configure as variáveis de ambiente ou edite application.properties
# com as credenciais do seu PostgreSQL local

# Build e execução
./mvnw spring-boot:run
```

---

## ⚙️ Variáveis de ambiente

Configure as seguintes variáveis no seu `application.properties` ou via `.env`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/conectaong
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

---

## 📁 Estrutura do projeto
```
altrua/
├── src/
│   └── main/
│       ├── java/com/techfun/altrua/
│       │   ├── controller/      # Endpoints REST
│       │   ├── service/         # Regras de negócio
│       │   ├── repository/      # Acesso ao banco (JPA)
│       │   ├── model/           # Entidades JPA
│       │   └── dto/             # Objetos de transferência
│       └── resources/
│           └── application.properties
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

## 🔌 API REST

A API REST do projeto ainda está em desenvolvimento. Assim que os controllers forem implementados em `src/main/java`, a documentação detalhada dos endpoints (métodos, URLs e descrições) será adicionada aqui para refletir fielmente o código-fonte.

> Observação: os endpoints planejados podem sofrer alterações até a consolidação da primeira versão estável da API.
---

## 🐳 docker-compose.yml (exemplo)
```yaml
version: '3.8'
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: conectaong
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/altrua
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres

volumes:
  pgdata:
```

---

## 👥 Colaboradores

Projeto desenvolvido por:

| Nome              | GitHub                                      | RA FAPAM             |
|-------------------|---------------------------------------------|----------------------|
| Mateus Felipe Gonçalves   | [@mateusfg7](https://github.com/mateusfg7)    | 16349        |
| Gabriel Henrique Sousa Mendonça  | [@gabriel-mkv](https://github.com/gabriel-mkv)    | 16359       |

---

## 📄 Licença

Este projeto está sob a licença [GPL-3.0](LICENSE).

---

<p align="center">Feito com ❤️ para o Projeto Integrador FAPAM</p>
