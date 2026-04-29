package com.techfun.altrua.infra.security.userdetails;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.techfun.altrua.features.user.domain.model.User;
import com.techfun.altrua.features.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementação concreta de {@link UserDetailsService} e
 * {@link UserLookupService}.
 * 
 * <p>
 * Responsável por recuperar os dados do usuário a partir do banco de dados (via
 * {@link UserRepository})
 * e convertê-los em um objeto {@link UserDetails} (neste caso,
 * {@link UserPrincipal})
 * que o Spring Security possa compreender para fins de autenticação e
 * autorização.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService, UserLookupService {

    private final UserRepository userRepository;

    /**
     * Carrega um usuário pelo seu e-mail, utilizado como credencial de login no
     * fluxo
     * padrão de autenticação do Spring Security.
     *
     * <p>
     * Observação: o "username" principal exposto por
     * {@link UserPrincipal#getUsername()}
     * é o identificador único (UUID) do usuário. O e-mail é apenas a credencial
     * usada
     * para localizar o usuário durante o login.
     * </p>
     *
     * @param email o e-mail do usuário utilizado para login
     * @return {@link UserDetails} contendo os dados do usuário autenticado
     * @throws UsernameNotFoundException se nenhum usuário com o e-mail fornecido
     *                                   for encontrado
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new UserPrincipal(user);
    }

    /**
     * Carrega um usuário pelo seu identificador único (UUID).
     * Utilizado principalmente para validar a identidade do usuário extraída de um
     * token JWT.
     *
     * @param id o UUID do usuário
     * @return detalhes do usuário encontrados
     * @throws UsernameNotFoundException se o usuário não for encontrado no banco de
     *                                   dados
     */
    @Override
    public UserDetails loadById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new UserPrincipal(user);
    }
}
