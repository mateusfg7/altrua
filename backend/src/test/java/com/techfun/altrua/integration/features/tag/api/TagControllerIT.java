package com.techfun.altrua.integration.features.tag.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.techfun.altrua.features.tag.api.TagController;
import com.techfun.altrua.features.tag.api.dto.TagResponseDTO;
import com.techfun.altrua.features.tag.service.TagService;
import com.techfun.altrua.infra.config.handler.GlobalExceptionHandler;
import com.techfun.altrua.integration.BaseControllerTest;

/**
 * Testes de integração para a camada web de gerenciamento de etiquetas (Tags).
 * Valida os contratos dos endpoints públicos, a formatação das respostas JSON
 * e a ordenação dos dados retornados, sem carregar o contexto completo de
 * segurança.
 */
@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("API: TagController")
class TagControllerIT extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

    @Nested
    @DisplayName("GET /tags")
    class ListAll {

        /**
         * Garante que a API responde com status 200 e serializa a lista de tags
         * corretamente quando houver registros cadastrados no sistema.
         */
        @Test
        @DisplayName("deve retornar 200 e a lista de tags em ordem alfabética")
        void shouldReturn200AndTagsListWhenTagsExist() throws Exception {
            when(tagService.listAllTags()).thenReturn(List.of(
                    new TagResponseDTO(UUID.randomUUID(), "educação"),
                    new TagResponseDTO(UUID.randomUUID(), "saúde")));

            mockMvc.perform(get("/tags"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("educação"))
                    .andExpect(jsonPath("$[1].name").value("saúde"));
        }

        /**
         * Verifica o comportamento nominal da API para cenários de base vazia,
         * garantindo o retorno de um array JSON vazio em vez de um valor nulo.
         */
        @Test
        @DisplayName("deve retornar 200 e lista vazia quando não houver tags")
        void shouldReturn200AndEmptyListWhenNoTagsExist() throws Exception {
            when(tagService.listAllTags()).thenReturn(List.of());

            mockMvc.perform(get("/tags"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }
}