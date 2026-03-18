package com.techfun.altrua.security.userdetails;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interface de serviço para busca de usuários por ID.
 * Utilizada principalmente para autenticação via JWT, onde o ID do usuário é extraído do token.
 */
public interface UserLookupService {
    
    /**
     * Carrega os detalhes do usuário com base no seu identificador único.
     *
     * @param id o UUID do usuário
     * @return objeto {@link UserDetails} correspondente ao usuário
     */
    UserDetails loadById(UUID id);
}
