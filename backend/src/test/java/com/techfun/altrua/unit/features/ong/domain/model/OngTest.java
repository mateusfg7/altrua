package com.techfun.altrua.unit.features.ong.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.techfun.altrua.core.common.exceptions.DomainException;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.ong.domain.model.OngAdministrator;
import com.techfun.altrua.features.user.domain.model.User;

/**
 * Testes de unidade para o domínio de ONGs e seus vínculos administrativos.
 * Garante a integridade das regras de negócio em memória e a consistência das
 * relações bidirecionais.
 */
@DisplayName("Domínio: Ong")
class OngTest {

    private Ong ong;
    private User user;

    @BeforeEach
    void setUp() {
        ong = Ong.builder()
                .name("Instituto Esperança")
                .build();

        ReflectionTestUtils.setField(ong, "id", UUID.randomUUID());

        user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
    }

    // -------------------------------------------------------------------------
    // Gestão de Administradores (Regras da Entidade Ong)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Ao gerenciar administradores na ONG")
    class AdministratorManagement {

        /**
         * Valida se a inclusão de um administrador reflete em ambos os lados da
         * associação JPA.
         */
        @Test
        @DisplayName("deve adicionar administrador e garantir o vínculo bidirecional")
        void shouldAddAdministratorAndSyncLink() {
            var admin = OngAdministrator.createAdministrator(user, ong);

            assertThat(ong.getAdministrators()).contains(admin);
            assertThat(admin.getOng()).isSameAs(ong);
        }

        /**
         * Verifica a trava de segurança que impede a exclusão do responsável original
         * da ONG.
         */
        @Test
        @DisplayName("deve lançar DomainException ao tentar remover o administrador criador")
        void shouldThrowExceptionWhenRemovingCreator() {
            var creator = OngAdministrator.createCreator(user, ong);

            assertThatThrownBy(() -> ong.removeAdministrator(creator))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("criador");

            assertThat(ong.getAdministrators()).contains(creator);
        }

        /**
         * Garante que a remoção de um administrador comum limpa as referências em
         * memória.
         */
        @Test
        @DisplayName("deve remover administrador comum e limpar a referência inversa")
        void shouldRemoveCommonAdministrator() {
            var admin = OngAdministrator.createAdministrator(user, ong);

            ong.removeAdministrator(admin);

            assertThat(ong.getAdministrators()).isEmpty();
            assertThat(admin.getOng()).isNull();
        }

        /**
         * Protege contra NullPointerException em operações de lista.
         */
        @Test
        @DisplayName("deve ignorar a adição caso o objeto administrador seja nulo")
        void shouldIgnoreNullAdministrator() {
            ong.addAdministrator(null);
            assertThat(ong.getAdministrators()).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // Criação de Vínculos (Regras da Entidade OngAdministrator)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Ao criar instâncias de OngAdministrator")
    class AdministratorCreation {

        /**
         * Valida a distinção entre administradores comuns e criadores via factory
         * methods.
         */
        @Test
        @DisplayName("deve configurar corretamente a flag de criador")
        void shouldSetCreatorFlagCorrectly() {
            var creator = OngAdministrator.createCreator(user, ong);
            var common = OngAdministrator.createAdministrator(user, ong);

            assertThat(creator.isCreator()).isTrue();
            assertThat(common.isCreator()).isFalse();
        }

        /**
         * Verifica se os IDs das entidades relacionadas são propagados para a chave
         * composta.
         */
        @Test
        @DisplayName("deve popular a chave composta com os IDs do usuário e da ONG")
        void shouldPopulateCompositeId() {
            var admin = OngAdministrator.createAdministrator(user, ong);

            assertThat(admin.getId().getUserId()).isEqualTo(user.getId());
            assertThat(admin.getId().getOngId()).isEqualTo(ong.getId());
        }

        /**
         * Garante que o construtor estático executa a lógica de associação na entidade
         * Ong.
         */
        @Test
        @DisplayName("deve garantir que o vínculo seja adicionado à lista da ONG no momento da criação")
        void shouldAutoLinkToOngOnCreation() {
            var admin = OngAdministrator.createAdministrator(user, ong);
            assertThat(ong.getAdministrators()).contains(admin);
        }
    }

    // -------------------------------------------------------------------------
    // Integridade Estrutural
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Ao validar integridade")
    class Integrity {

        /**
         * Evita NullPointerException ao garantir que coleções nunca nasçam nulas via
         * Builder.
         */
        @Test
        @DisplayName("deve inicializar a lista de administradores como vazia via Builder")
        void shouldStartWithEmptySet() {
            Ong newOng = Ong.builder().build();
            assertThat(newOng.getAdministrators()).isNotNull().isEmpty();
        }
    }
}