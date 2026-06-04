package com.techfun.altrua.integration;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;

/**
 * Classe base para testes de integração da plataforma Altrua.
 *
 * <p>Utiliza um banco PostgreSQL real provisionado pelo CI (ou localmente),
 * configurado via perfil Spring {@code test} e variáveis de ambiente.</p>
 *
 * <b>Configurações aplicadas:</b>
 * <ul>
 *   <li>Perfil Spring {@code test} ativado.</li>
 *   <li>Ambiente MockMvc configurado para testes de endpoints.</li>
 *   <li>Limpeza automática do banco após cada método de teste.</li>
 * </ul>
 *
 * <b>Uso:</b>
 * As classes que estendem esta base não precisam se preocupar com limpeza
 * de estado, a menos que possuam tabelas específicas não listadas em
 * {@link #cleanDatabase(JdbcTemplate)}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
public abstract class IntegrationTestBase {

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