package com.techfun.altrua.features.ong.service;

import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techfun.altrua.core.common.exceptions.DuplicateResourceException;
import com.techfun.altrua.core.common.exceptions.ForbiddenActionException;
import com.techfun.altrua.core.common.util.SlugUtils;
import com.techfun.altrua.features.ong.api.OngSpecification;
import com.techfun.altrua.features.ong.api.dto.OngFilterDTO;
import com.techfun.altrua.features.ong.api.dto.OngResponseDTO;
import com.techfun.altrua.features.ong.api.dto.RegisterOngRequestDTO;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.ong.domain.model.OngAdministrator;
import com.techfun.altrua.features.ong.domain.model.OngAdministratorId;
import com.techfun.altrua.features.ong.repository.OngAdministratorRepository;
import com.techfun.altrua.features.ong.repository.OngRepository;
import com.techfun.altrua.features.user.domain.User;

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
@Transactional(readOnly = true)
public class OngService {

    private final OngRepository ongRepository;
    private final OngAdministratorRepository ongAdministratorRepository;

    /**
     * Registra uma nova organização (ONG) e estabelece seu administrador inicial.
     *
     * <p>
     * O fluxo compreende a normalização do nome para geração de slug, a validação
     * de unicidade de registros ativos (CNPJ e Slug) e o vínculo automático do
     * criador como administrador principal da entidade.
     * </p>
     *
     * @param request DTO com os dados de entrada validados.
     * @param creator Usuário autenticado que será definido como administrador e
     *                criador.
     * @return A entidade {@link Ong} persistida e configurada.
     * @throws DuplicateResourceException Se o CNPJ ou o Slug gerado já pertencerem
     *                                    a uma ONG ativa.
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

                if ("uk_active_ong_slug".equals(cve.getConstraintName())) {
                    log.warn("Conflito de slug ao cadastrar ONG: {}", slug);
                    throw new DuplicateResourceException("Já existe uma organização com este nome/slug ativa.");
                }

                if ("uk_active_ong_cnpj".equals(cve.getConstraintName())) {
                    log.warn("Conflito de CNPJ ao cadastrar ONG: {}", request.cnpj());
                    throw new DuplicateResourceException("O CNPJ informado já está vinculado a uma organização ativa.");
                }
            }

            log.error("Erro técnico inesperado ao cadastrar ONG: {}", ex.getMessage());
            throw ex;
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
    public Page<OngResponseDTO> listNgos(OngFilterDTO filter, Pageable pageable) {
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
     * @throws ForbiddenActionException Se o usuário não estiver registrado como
     *                                  administrador da ONG informada.
     */
    public void validateAdminPermission(UUID ongId, UUID userId) {
        OngAdministratorId id = new OngAdministratorId(userId, ongId);

        if (!ongAdministratorRepository.existsById(id)) {
            log.warn("Tentativa de acesso não autorizado: Usuário {} na ONG {}", userId, ongId);
            throw new ForbiddenActionException("Você não tem permissão para realizar essa ação.");
        }
    }
}
