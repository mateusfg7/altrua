package com.techfun.altrua.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Classe de configuração de segurança do Spring Security.
 * Define as regras de autorização, filtros e codificação de senhas.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura a cadeia de filtros de segurança (Security Filter Chain).
     *
     * <p>
     * Desabilita CSRF, define a política de sessão como STATELESS e configura
     * as permissões de acesso aos endpoints (Swagger e API docs públicos).
     * </p>
     *
     * @param http o objeto {@link HttpSecurity} para configuração
     * @return a cadeia de filtros configurada
     * @throws Exception em caso de erro na configuração
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()
                        .anyRequest().permitAll());
        return http.build();
    }

    /**
     * Define o encoder de senhas da aplicação.
     *
     * <p>
     * Utiliza o algoritmo Argon2, recomendado para armazenamento seguro de senhas.
     * </p>
     *
     * @return instância configurada de {@link Argon2PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
            16,   // salt length
            32,   // hash length
            1,    // parallelism
            65536, // memory (64 MB)
            3     // iterations
        );
    }
}
