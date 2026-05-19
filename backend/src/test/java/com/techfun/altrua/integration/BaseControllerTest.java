package com.techfun.altrua.integration;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.techfun.altrua.infra.security.jwt.JwtValidator;
import com.techfun.altrua.infra.security.userdetails.UserLookupService;

/**
 * Classe base para testes de integração de fatia web (Web Slice Tests /
 * Controllers).
 * 
 * <p>
 * Fornece a infraestrutura necessária para inicializar o contexto do Spring
 * MVC, importando as configurações de teste e mockando os feijões (beans)
 * globais de segurança para evitar falhas de inicialização do contexto de
 * segurança do Spring.
 * </p>
 */
@Import(TestConfig.class)
public abstract class BaseControllerTest {

    @MockitoBean
    protected JwtValidator jwtValidator;

    @MockitoBean
    protected UserLookupService userLookupService;
}
