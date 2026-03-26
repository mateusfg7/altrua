package com.techfun.altrua.ong;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.techfun.altrua.common.exceptions.DuplicateResourceException;
import com.techfun.altrua.common.util.SlugUtils;
import com.techfun.altrua.ong.dto.RegisterOngRequestDTO;
import com.techfun.altrua.user.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pela lógica de negócios relacionada às ONGs.
 * 
 * <p>
 * Gerencia o ciclo de vida das organizações, incluindo validação de unicidade
 * de documentos (CNPJ), geração de slugs amigáveis e vinculação de
 * administradores.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class OngService {

    private final OngRepository ongRepository;

    /**
     * Realiza o registro de uma nova ONG no sistema.
     * 
     * <p>
     * O processo inclui:
     * 1. Validação de CNPJ duplicado.
     * 2. Geração e normalização de slug a partir do nome.
     * 3. Tratamento de colisão de slugs com sufixos aleatórios.
     * 4. Atribuição do usuário solicitante como administrador e criador.
     * </p>
     *
     * @param request dados da nova ONG vindos da requisição
     * @param creator entidade do usuário que está criando a ONG
     * @return a entidade {@link Ong} persistida
     * @throws DuplicateResourceException se o CNPJ já existir ou se houver falha
     *                                    de integridade ao salvar
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
            throw new DuplicateResourceException("Erro ao cadastrar ONG.");
        }
    }
}
