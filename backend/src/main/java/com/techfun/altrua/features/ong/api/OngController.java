package com.techfun.altrua.features.ong.api;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techfun.altrua.features.ong.api.dto.OngFilterDTO;
import com.techfun.altrua.features.ong.api.dto.OngResponseDTO;
import com.techfun.altrua.features.ong.api.dto.RegisterOngRequestDTO;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.ong.service.OngService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST responsável por gerenciar as operações relacionadas às ONGs.
 * 
 * <p>
 * Oferece endpoints para o registro de novas organizações e, futuramente,
 * para consulta e edição de dados institucionais.
 * </p>
 */
@RestController
@RequestMapping("/ongs")
@RequiredArgsConstructor
public class OngController {

    private final OngService ongService;

    /**
     * Endpoint para o registro de uma nova ONG.
     * 
     * <p>
     * Este método exige que o usuário esteja autenticado, vinculando-o
     * automaticamente como o administrador criador da organização.
     * </p>
     *
     * @param dto objeto contendo os dados cadastrais da ONG
     * @return {@link ResponseEntity} contendo os dados da ONG criada com status 201
     *         (Created)
     */
    @PostMapping
    public ResponseEntity<OngResponseDTO> register(@RequestBody @Valid RegisterOngRequestDTO dto) {
        Ong savedOng = ongService.register(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(OngResponseDTO.fromEntity(savedOng));
    }

    /**
     * Endpoint para listagem paginada de ONGs com suporte a filtros dinâmicos.
     * <p>
     * O Spring realiza o bind automático dos parâmetros da query string para o
     * objeto
     * {@link OngFilterDTO}. Caso nenhum parâmetro seja enviado, o objeto é
     * instanciado
     * com campos nulos, resultando em uma listagem completa.
     * </p>
     *
     * @param filter   Objeto contendo os critérios de filtragem (extraídos da URL).
     * @param pageable Configurações de paginação e ordenação (padrão: 10 registros
     *                 por página).
     * @return {@link ResponseEntity} contendo a página de {@link OngResponseDTO}.
     */
    @GetMapping
    public ResponseEntity<Page<OngResponseDTO>> list(
            @ParameterObject OngFilterDTO filter,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ongService.listNgos(filter, pageable));
    }
}
