package com.techfun.altrua.infra.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Define a arquitetura de segurança da API utilizando uma política
     * <b>Stateless</b> e autenticação via <b>JWT</b>.
     * Este método desabilita a proteção contra CSRF (visto que a API não utiliza
     * sessões baseadas em cookies),
     * estabelece a configuração de CORS e centraliza o controle de acesso.
     * A cadeia de filtros é estendida pela injeção do
     * {@code jwtAuthenticationFilter} precedendo o
     * {@link UsernamePasswordAuthenticationFilter}, garantindo que a validação do
     * token ocorra antes
     * do processo de autenticação padrão do Spring.
     *
     * @param http o objeto {@link HttpSecurity} para customização da segurança.
     * @return a instância de {@link SecurityFilterChain} configurada para o
     *         contexto da aplicação.
     * @throws Exception caso ocorra uma falha na construção da hierarquia de
     *                   filtros ou na injeção de provedores.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/ongs").permitAll()
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
     * Define a política global de CORS para a aplicação.
     * <ul>
     * <li><b>Origens:</b> Baseado na lista {@code allowedOrigins}.</li>
     * <li><b>Métodos:</b> GET, POST, PUT, DELETE e OPTIONS.</li>
     * <li><b>Headers:</b> Todos permitidos ({@code *}).</li>
     * <li><b>Cache:</b> Configurações válidas por 1 hora (3600s).</li>
     * </ul>
     * @return {@link CorsConfigurationSource} mapeado para todos os caminhos
     * ({@code /**}).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
