package com.techfun.altrua.ong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techfun.altrua.ong.dto.OngResponseDTO;
import com.techfun.altrua.ong.dto.RegisterOngRequestDTO;
import com.techfun.altrua.security.userdetails.UserPrincipal;

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
     * @param dto           objeto contendo os dados cadastrais da ONG
     * @param userPrincipal detalhes do usuário autenticado obtidos via Spring
     *                      Security
     * @return {@link ResponseEntity} contendo os dados da ONG criada com status 201
     *         (Created)
     */
    @PostMapping
    public ResponseEntity<OngResponseDTO> register(@RequestBody @Valid RegisterOngRequestDTO dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Ong savedOng = ongService.register(dto, userPrincipal.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(OngResponseDTO.fromEntity(savedOng));
    }

}
