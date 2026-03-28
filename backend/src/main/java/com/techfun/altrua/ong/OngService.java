package com.techfun.altrua.ong;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.techfun.altrua.common.exceptions.DuplicateResourceException;
import com.techfun.altrua.common.util.SlugUtils;
import com.techfun.altrua.ong.dto.RegisterOngRequestDTO;
import com.techfun.altrua.user.User;

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
            Ong ong = Ong.builder()
                    .name(request.name())
                    .slug(slug)
                    .cnpj(request.cnpj())
                    .description(request.description())
                    .email(request.email())
                    .phone(request.phone())
                    .category(request.category())
                    .status(OngStatusEnum.ATIVA)
                    .logoUrl(request.logoUrl())
                    .bannerUrl(request.bannerUrl())
                    .donationInfo(request.donationInfo())
                    .latitude(request.latitude())
                    .longitude(request.longitude())
                    .build();

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
}
