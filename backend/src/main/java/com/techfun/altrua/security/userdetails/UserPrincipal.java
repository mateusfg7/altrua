package com.techfun.altrua.security.userdetails;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.techfun.altrua.entities.User;

import lombok.RequiredArgsConstructor;

/**
 * Implementação de {@link UserDetails} para integração com o Spring Security.
 * 
 * <p>
 * Envolve a entidade {@link User} e adapta seus dados para os contratos de
 * autenticação
 * e autorização do framework.
 * </p>
 */
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final User user;

    /**
     * Retorna a senha do usuário.
     *
     * @return senha criptografada
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Retorna o identificador único do usuário (UUID) utilizado como subject do
     * JWT.
     *
     * <p>
     * Embora o método se chame {@code getUsername} por contrato da interface
     * {@link UserDetails}, nesta implementação retorna o ID do usuário para
     * garantir imutabilidade do subject do token.
     * </p>
     *
     * @return UUID do usuário convertido para String
     */
    @Override
    public String getUsername() {
        return user.getId().toString();
    }

    /**
     * Retorna as autoridades (roles/permissões) concedidas ao usuário.
     *
     * @return uma lista contendo a role "ROLE_USER" por padrão
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * Indica se a conta do usuário expirou.
     *
     * @return {@code true} (sempre ativo nesta implementação)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica se o usuário está bloqueado ou não.
     *
     * @return {@code true} (sempre desbloqueado nesta implementação)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica se as credenciais (senha) do usuário expiraram.
     *
     * @return {@code true} (sempre válidas nesta implementação)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica se o usuário está habilitado ou desabilitado.
     *
     * @return {@code true} (sempre habilitado nesta implementação)
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Recupera a entidade de usuário subjacente.
     *
     * @return a entidade {@link User}
     */
    public User getUser() {
        return user;
    }
}
