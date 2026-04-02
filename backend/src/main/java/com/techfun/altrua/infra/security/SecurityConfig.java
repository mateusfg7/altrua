package com.techfun.altrua.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.techfun.altrua.infra.security.filter.JwtAuthenticationFilter;
import com.techfun.altrua.infra.security.handler.CustomAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

/**
 * Classe de configuração de segurança do Spring Security.
 * Define as regras de autorização, filtros e codificação de senhas.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

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
                                "/api-docs/**",
                                "/docs/**",
                                "/auth/signup",
                                "/auth/login",
                                "/auth/refresh")
                        .permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Configura o provedor de autenticação (AuthenticationProvider).
     *
     * <p>
     * Utiliza o {@link DaoAuthenticationProvider} para realizar a autenticação
     * baseada em banco de dados, vinculando o serviço de detalhes do usuário
     * e o codificador de senha.
     * </p>
     *
     * @return instância configurada do provedor de autenticação
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Expõe o gerenciador de autenticação (AuthenticationManager) como um Bean.
     *
     * @param config configuração de autenticação do Spring
     * @return o gerenciador de autenticação padrão
     * @throws Exception caso ocorra erro ao obter o gerenciador
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
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
                16, // salt length
                32, // hash length
                1, // parallelism
                65536, // memory (64 MB)
                3 // iterations
        );
    }
}
