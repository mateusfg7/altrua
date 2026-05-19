package com.techfun.altrua.integration;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Classe base para Testes de Integração (IT) da plataforma Altrua.
 * <p>
 * Esta classe implementa o padrão <b>Singleton Container</b> via
 * Testcontainers, garantindo que apenas uma instância do PostgreSQL seja
 * iniciada para toda a suíte de testes, reduzindo drasticamente o tempo de
 * execução.
 * </p>
 * 
 * <b>Configurações Aplicadas:</b>
 * <ul>
 * <li>Habilita o perfil Spring {@code test}.</li>
 * <li>Configura o ambiente MockMvc para testes de endpoints.</li>
 * <li>Sobe um container Docker {@code postgres:18-alpine}.</li>
 * <li>Limpa automaticamente o estado do banco de dados após cada método de
 * teste.</li>
 * </ul>
 * 
 * <b>Uso:</b>
 * As classes que estendem esta base não precisam se preocupar com o ciclo de
 * vida do banco ou limpeza de tabelas, a menos que existam tabelas específicas
 * não listadas no método {@link #cleanDatabase(JdbcTemplate)}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
public abstract class IntegrationTestBase {

    /**
     * Container PostgreSQL compartilhado.
     * Declarado como estático e iniciado em bloco static para persistir
     * durante toda a execução da JVM (Singleton).
     */
    protected static final PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:18-alpine")
                .withDatabaseName("altrua_test")
                .withUsername("test")
                .withPassword("test");
        postgres.start();
    }

    /**
     * Injeta dinamicamente as credenciais e a URL de conexão do container
     * nas propriedades do Spring Data JPA.
     * 
     * @param registry Registro de propriedades dinâmicas.
     */
    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    /**
     * Garante o isolamento dos testes limpando as tabelas principais.
     * Executado automaticamente após cada @Test.
     * 
     * <p>
     * <b>Ordem de Limpeza:</b> Deve respeitar as restrições de chave estrangeira
     * (primeiro as tabelas filhas, depois as pais).
     * </p>
     * 
     * @param jdbcTemplate Template injetado para execução de scripts de limpeza.
     */
    @AfterEach
    void cleanDatabase(@Autowired JdbcTemplate jdbcTemplate) {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "refresh_tokens", "ong_administrators", "ongs", "users", "tags");
    }
}
