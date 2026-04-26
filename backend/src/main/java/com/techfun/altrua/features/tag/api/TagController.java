package com.techfun.altrua.features.tag.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techfun.altrua.features.tag.api.dto.TagResponseDTO;
import com.techfun.altrua.features.tag.service.TagService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST responsável por expor as operações relacionadas às etiquetas
 * (tags).
 * <p>
 * Este controlador fornece endpoints para a recuperação de metadados de
 * categorização,
 * essenciais para a composição de componentes de filtragem dinâmica no
 * frontend.
 * </p>
 */
@Tag(name = "Tags", description = "Endpoints para gerenciamento e listagem de etiquetas de categorização")
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * Recupera a listagem completa de tags cadastradas no sistema.
     * <p>
     * Os dados são retornados sem paginação e em ordem alfabética para garantir
     * consistência visual nos componentes de seleção da interface do usuário.
     * </p>
     * 
     * @return {@link ResponseEntity} contendo uma {@link List} de
     *         {@link TagResponseDTO}.
     */
    @Operation(summary = "Lista todas as tags disponíveis", description = "Retorna uma lista de todas as tags cadastradas no sistema, ordenadas alfabeticamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tags recuperada com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<TagResponseDTO>> listAll() {
        List<TagResponseDTO> tags = tagService.listAllTags();
        return ResponseEntity.ok(tags);
    }
}
