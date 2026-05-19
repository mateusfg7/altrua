package com.techfun.altrua.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Configuração específica para o ambiente de testes.
 * <p>
 * Esta classe utiliza {@link TestConfiguration} para sobrepor ou adicionar
 * beans ao contexto do Spring apenas durante a execução de testes integrados,
 * sem interferir na configuração principal da aplicação.
 * </p>
 * 
 * @see com.techfun.altrua.integration.IntegrationTestBase
 */
@TestConfiguration
public class TestConfig {

    /**
     * Define um {@link ObjectMapper} customizado para os testes.
     * <p>
     * As customizações garantem que:
     * <ul>
     * <li>Módulos do Java 8 (como JSR-310 para LocalDateTime) sejam registrados
     * automaticamente.</li>
     * <li>Datas sejam serializadas no formato ISO-8601 (String) em vez de
     * timestamps numéricos,
     * facilitando asserções em JSON e legibilidade dos logs.</li>
     * </ul>
     * </p>
     * 
     * @return Uma instância de {@link ObjectMapper} configurada para legibilidade e
     *         compatibilidade com tipos modernos do Java.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}