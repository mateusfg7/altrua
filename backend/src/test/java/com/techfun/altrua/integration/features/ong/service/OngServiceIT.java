package com.techfun.altrua.integration.features.ong.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.techfun.altrua.core.common.exceptions.DuplicateResourceException;
import com.techfun.altrua.features.ong.api.dto.OngResponseDTO;
import com.techfun.altrua.features.ong.api.dto.RegisterOngRequestDTO;
import com.techfun.altrua.features.ong.repository.OngAdministratorRepository;
import com.techfun.altrua.features.ong.repository.OngRepository;
import com.techfun.altrua.features.ong.service.OngService;
import com.techfun.altrua.features.user.domain.model.User;
import com.techfun.altrua.features.user.repository.UserRepository;
import com.techfun.altrua.infra.security.userdetails.UserPrincipal;
import com.techfun.altrua.integration.IntegrationTestBase;

/**
 * Testes de integração para o gerenciamento de ONGs.
 * Valida o fluxo de registro, regras de unicidade de CNPJ, normalização de
 * slugs
 * e a vinculação automática de administradores via contexto de segurança.
 */
@DisplayName("Integração: OngService")
class OngServiceIT extends IntegrationTestBase {

    @Autowired
    private OngService ongService;

    @Autowired
    private OngRepository ongRepository;

    @Autowired
    private OngAdministratorRepository ongAdministratorRepository;

    @Autowired
    private UserRepository userRepository;

    private User authenticatedUser;

    @BeforeEach
    void setUp() {
        authenticatedUser = userRepository.save(buildUser("Creator", "creator@altrua.com"));
        authenticateAs(authenticatedUser);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // -------------------------------------------------------------------------
    // Cenários de sucesso
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Dado um request válido")
    class Success {

        /**
         * Valida a persistência básica e a conversão para DTO de resposta.
         */
        @Test
        @DisplayName("deve persistir a ONG e retornar o DTO com os dados salvos")
        void shouldPersistOngAndReturnDto() {
            var request = buildRequest("Instituto Esperança", "12345678000199");

            OngResponseDTO result = ongService.register(request);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Instituto Esperança");
            assertThat(ongRepository.count()).isEqualTo(1);
        }

        /**
         * Garante que o usuário logado no momento da criação seja registrado como
         * administrador.
         */
        @Test
        @DisplayName("deve vincular o usuário autenticado como administrador criador")
        void shouldLinkCreatorAsAdministrator() {
            var request = buildRequest("Instituto Esperança", null);

            OngResponseDTO result = ongService.register(request);

            boolean isAdmin = ongAdministratorRepository
                    .existsById(new com.techfun.altrua.features.ong.domain.model.OngAdministratorId(
                            authenticatedUser.getId(), result.id()));

            assertThat(isAdmin).isTrue();
        }

        /**
         * Valida a opcionalidade do CNPJ no registro.
         */
        @Test
        @DisplayName("quando CNPJ é nulo, deve registrar sem erro")
        void shouldRegisterWithoutCnpj() {
            var request = buildRequest("ONG Sem CNPJ", null);

            OngResponseDTO result = ongService.register(request);

            assertThat(result).isNotNull();
            assertThat(ongRepository.count()).isEqualTo(1);
        }

        /**
         * Testa o mecanismo de resolução de conflitos de slug para nomes idênticos.
         */
        @Test
        @DisplayName("quando slug já existe, deve registrar com sufixo e não sobrescrever a ONG existente")
        void shouldUseSlugWithSuffixWhenAlreadyExists() {
            ongService.register(buildRequest("Instituto Esperança", null));
            ongService.register(buildRequest("Instituto Esperança", "98765432000100"));

            assertThat(ongRepository.count()).isEqualTo(2);

            var slugs = ongRepository.findAll().stream()
                    .map(o -> o.getSlug())
                    .toList();

            assertThat(slugs).doesNotHaveDuplicates();
            assertThat(slugs).anyMatch(s -> s.equals("instituto-esperanca"));
            assertThat(slugs).anyMatch(s -> s.startsWith("instituto-esperanca-"));
        }
    }

    // -------------------------------------------------------------------------
    // Cenários de erro — CNPJ duplicado
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Dado um CNPJ já cadastrado")
    class DuplicatedCnpj {

        /**
         * Garante a integridade dos dados impedindo duplicidade de CNPJ.
         */
        @Test
        @DisplayName("deve lançar DuplicateResourceException e não criar segunda ONG")
        void shouldThrowExceptionAndNotPersist() {
            String cnpj = "12345678000199";
            ongService.register(buildRequest("ONG Original", cnpj));

            assertThatThrownBy(() -> ongService.register(buildRequest("ONG Duplicada", cnpj)))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("CNPJ");

            assertThat(ongRepository.count()).isEqualTo(1);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Simula a autenticação de um usuário no SecurityContext do Spring.
     */
    private void authenticateAs(User user) {
        var userPrincipal = new UserPrincipal(user);
        var auth = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private User buildUser(String name, String email) {
        return User.createStandard(name, email, "{noop}senha-fake");
    }

    private RegisterOngRequestDTO buildRequest(String name, String cnpj) {
        return new RegisterOngRequestDTO(
                name,
                cnpj,
                "contato@teste.org",
                "Proteção Animal",
                null, null, null, null, null, null, null);
    }

}