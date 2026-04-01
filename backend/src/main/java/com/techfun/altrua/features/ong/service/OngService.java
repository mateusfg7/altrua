package com.techfun.altrua.features.ong.service;

import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.techfun.altrua.core.common.exceptions.DuplicateResourceException;
import com.techfun.altrua.core.common.util.SlugUtils;
import com.techfun.altrua.features.ong.api.OngSpecification;
import com.techfun.altrua.features.ong.api.dto.OngFilterDTO;
import com.techfun.altrua.features.ong.api.dto.OngResponseDTO;
import com.techfun.altrua.features.ong.api.dto.RegisterOngRequestDTO;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.ong.domain.model.OngAdministrator;
import com.techfun.altrua.features.ong.repository.OngAdministratorRepository;
import com.techfun.altrua.features.ong.repository.OngRepository;
import com.techfun.altrua.features.user.domain.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável pela lógica de negócios relacionada às ONGs.
 * 
 * <p>
 * Gerencia o ciclo de vida das organizações, incluindo validação de unicidade
 * de documentos (CNPJ), geração de slugs amigáveis e vinculação de
 * administradores.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OngService {

    private final OngRepository ongRepository;
    private final OngAdministratorRepository ongAdministratorRepository;

    /**
     * Registra uma nova ONG no sistema.
     *
     * <p>
     * Valida duplicidade de CNPJ, gera um slug único a partir do nome e persiste
     * a ONG com o usuário solicitante como administrador principal.
     * </p>
     *
     * @param request dados da ONG a ser cadastrada
     * @param creator usuário responsável pelo cadastro, definido como administrador
     * @return a entidade {@link Ong} persistida
     * @throws DuplicateResourceException se o CNPJ já estiver cadastrado ou houver
     *                                    conflito de unicidade ao persistir
     */
    @Transactional
    public Ong register(RegisterOngRequestDTO request, User creator) {
        if (request.cnpj() != null && ongRepository.existsByCnpj(request.cnpj())) {
            throw new DuplicateResourceException("CNPJ já cadastrado.");
        }

        String slug = SlugUtils.normalize(request.name());

        if (ongRepository.existsBySlug(slug)) {
            slug = SlugUtils.withSuffix(slug);
        }

        try {
            Ong ong = request.toEntity(slug);
            OngAdministrator admin = new OngAdministrator(creator, ong, true);
            ong.addAdministrator(admin);
            return ongRepository.save(ong);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof ConstraintViolationException cve) {
                log.warn("Conflito de unicidade ao cadastrar ONG. Constraint: {}", cve.getConstraintName());
                throw new DuplicateResourceException(
                        "Já existe uma ONG cadastrada com os dados fornecidos.");
            }

            log.error("Erro técnico inesperado ao cadastrar ONG", ex);
            throw new RuntimeException("Não foi possível processar o cadastro da ONG no momento.");
        }
    }

    /**
     * Recupera uma página de ONGs filtradas de acordo com os critérios fornecidos.
     * <p>
     * O processo executa três etapas:
     * 1. Constrói a especificação dinâmica via {@link OngSpecification}.
     * 2. Consulta o banco de dados aplicando filtros e metadados de paginação.
     * 3. Converte o resultado da entidade {@link Ong} para {@link OngResponseDTO}.
     * </p>
     *
     * @param filter   Objeto contendo os critérios de busca (nome, slug, etc.).
     * @param pageable Configurações de paginação (página atual, tamanho,
     *                 ordenação).
     * @return Uma página de {@link OngResponseDTO} contendo os registros
     *         encontrados.
     * @throws org.springframework.dao.DataAccessException em caso de erro na
     *                                                     persistência.
     */
    public Page<OngResponseDTO> listOngs(OngFilterDTO filter, Pageable pageable) {
        Specification<Ong> spec = OngSpecification.withFilter(filter);
        return ongRepository.findAll(spec, pageable).map(OngResponseDTO::fromEntity);
    }

    /**
     * Valida se um usuário possui privilégios de administrador para uma ONG
     * específica.
     * <p>
     * Realiza uma consulta de existência na base de dados para verificar o vínculo
     * de
     * administração. Caso o vínculo não exista, um log de aviso é gerado para fins
     * de
     * auditoria e segurança.
     * </p>
     *
     * @param ongId  O identificador único da ONG alvo da operação.
     * @param userId O identificador único do usuário que tenta realizar a ação.
     * @throws AccessDeniedException Se o usuário não estiver registrado como
     *                               administrador da ONG informada.
     */
    public void validateAdminPermission(UUID ongId, UUID userId) {
        if (!ongAdministratorRepository.existsByOngIdAndUserId(ongId, userId)) {
            log.warn("Tentativa de acesso não autorizado: Usuário {} na ONG {}", userId, ongId);
            throw new AccessDeniedException("Você não tem permissão para realizar essa ação.");
        }
    }
}
