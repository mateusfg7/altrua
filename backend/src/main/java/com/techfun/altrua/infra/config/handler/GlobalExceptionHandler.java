package com.techfun.altrua.infra.config.handler;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.techfun.altrua.core.common.exceptions.BusinessException;
import com.techfun.altrua.core.common.exceptions.DuplicateResourceException;
import com.techfun.altrua.core.common.exceptions.ForbiddenActionException;
import com.techfun.altrua.core.common.exceptions.InvalidCredentialsException;
import com.techfun.altrua.core.common.exceptions.RefreshTokenException;
import com.techfun.altrua.infra.security.handler.CustomAuthenticationEntryPoint;

import lombok.extern.slf4j.Slf4j;

/**
 * Manipulador global de exceções da API.
 * 
 * <p>
 * Centraliza o tratamento de erros disparados pelos Controllers, convertendo
 * exceções
 * em respostas padronizadas no formato {@link ProblemDetail} (RFC 7807).
 * Garante que o cliente receba sempre uma estrutura consistente,
 * independentemente do erro.
 * </p>
 * 
 * <p>
 * Estrutura padrão da resposta:
 * <ul>
 * <li><b>status:</b> Código HTTP.</li>
 * <li><b>instance:</b> URI da requisição.</li>
 * <li><b>title:</b> Resumo legível do tipo de erro.</li>
 * <li><b>detail:</b> Explicação específica do erro.</li>
 * <li><b>timestamp:</b> Instante exato da ocorrência.</li>
 * <li><b>invalid_params:</b> (Opcional) Mapa de erros de validação de
 * campos.</li>
 * </ul>
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Trata exceções genéricas de regras de negócio.
     * 
     * @param ex A exceção de negócio capturada.
     * @return {@link ProblemDetail} com o status e mensagem definidos na exceção.
     */
    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex) {
        return buildProblemDetail(ex.getStatus(), ex.getMessage(), "Erro de Regra de Negócio");
    }

    /**
     * Trata falhas de autenticação e acesso negado.
     * 
     * <p>
     * Captura tanto exceções nativas do Spring Security quanto a exceção
     * customizada
     * de credenciais, retornando sempre uma mensagem genérica por segurança.
     * </p>
     * 
     * @param ex Exceção de autenticação.
     * @return {@link ProblemDetail} com status 401 Unauthorized.
     */
    @ExceptionHandler({ BadCredentialsException.class, InvalidCredentialsException.class })
    public ProblemDetail handleInvalidCredencials(Exception ex) {
        return buildProblemDetail(HttpStatus.UNAUTHORIZED, "Credenciais inválidas", "Falha na Autenticação");
    }

    /**
     * Manipula falhas específicas durante a renovação do token de acesso (Refresh
     * Token).
     * <p>
     * Este método é acionado quando um {@code Refresh Token} é inválido, foi
     * revogado
     * ou expirou. Ao retornar {@code 401 Unauthorized}, a API sinaliza ao cliente
     * (ex: Frontend ou Mobile) que a sessão encerrou completamente e que uma nova
     * autenticação via credenciais (login) é estritamente necessária.
     * </p>
     *
     * @param ex A exceção contendo os detalhes da falha na renovação do token.
     * @return Um objeto {@link ProblemDetail} com status 401 (Unauthorized) e
     *         orientações sobre a falha na sessão.
     */
    @ExceptionHandler(RefreshTokenException.class)
    public ProblemDetail handleRefreshToken(RefreshTokenException ex) {
        log.warn("Falha no Refresh Token: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.UNAUTHORIZED, ex.getMessage(), "Falha na Autenticação");
    }

    /**
     * Manipula falhas de autenticação ocorridas durante o processamento da
     * requisição.
     * <p>
     * Este método intercepta exceções do tipo {@link AuthenticationException}, que
     * geralmente
     * são originadas nos filtros de segurança (ex: JWT) e delegadas a este handler
     * pelo
     * {@link CustomAuthenticationEntryPoint}. Retorna um erro padronizado
     * informando que
     * as credenciais são inválidas, expiraram ou não foram fornecidas.
     * </p>
     *
     * @param ex A exceção de autenticação capturada.
     * @return Um objeto {@link ProblemDetail} com status 401 (Unauthorized) e
     *         detalhes da falha.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        log.error("Falha de autenticação: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.UNAUTHORIZED, "Token de acesso inválido, expirado ou ausente.",
                "Falha na Autenticação");
    }

    /**
     * Trata tentativas de criação de recursos duplicados (ex: e-mail ou CNPJ já
     * cadastrado).
     * 
     * @param ex Exceção de conflito de recurso.
     * @return {@link ProblemDetail} com status 409 Conflict.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResource(DuplicateResourceException ex) {
        return buildProblemDetail(ex.getStatus(), ex.getMessage(), "Recurso Duplicado");
    }

    /**
     * Sobrescreve o tratamento de erros de validação do Bean Validation (@Valid).
     * 
     * <p>
     * Extrai os erros de campos específicos e os agrupa no campo customizado
     * 'invalid_params'.
     * </p>
     * 
     * @return {@link ResponseEntity} contendo o ProblemDetail com status 400 Bad
     *         Request.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ProblemDetail problem = buildProblemDetail(status, "Erro de validação nos campos", "Requisição Inválida");

        var fields = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existingMessage, newMessage) -> existingMessage));

        problem.setProperty("invalid_params", fields);
        return ResponseEntity.status(status).body(problem);
    }

    /**
     * Manipula violações de regras de negócio relacionadas a permissões de acesso.
     * <p>
     * Este método intercepta a {@link ForbiddenActionException} lançada pela camada
     * de
     * serviço quando um usuário, embora autenticado, tenta realizar uma operação
     * que viola as regras de domínio (ex: editar uma ONG da qual não é
     * administrador).
     * A mensagem detalhada é retornada ao cliente para fornecer feedback claro
     * sobre
     * o motivo da rejeição.
     * </p>
     *
     * @param ex A exceção de regra de negócio contendo o status HTTP e a mensagem
     *           específica.
     * @return Um objeto {@link ProblemDetail} formatado com o status da exceção e a
     *         mensagem de erro de negócio.
     */
    @ExceptionHandler(ForbiddenActionException.class)
    public ProblemDetail handleForbiddenAction(ForbiddenActionException ex) {
        return buildProblemDetail(ex.getStatus(), ex.getMessage(), "Ação Proibida");
    }

    /**
     * Manipula exceções de segurança lançadas pelo Spring Security
     * (infraestrutura).
     * <p>
     * Este método intercepta a {@link AccessDeniedException} quando o framework
     * barra o acesso
     * com base em roles, permissões de rota ou anotações de segurança
     * (ex: @PreAuthorize).
     * Por motivos de segurança, a mensagem detalhada da exceção é ocultada do
     * cliente final
     * e apenas registrada em log para auditoria.
     * </p>
     *
     * @param ex A exceção de acesso negado original capturada pelo framework.
     * @return Um objeto {@link ProblemDetail} com status 403 e mensagem genérica
     *         padronizada.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        log.error("Segurança: Acesso negado via Spring Security - {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este recurso.",
                "Acesso Negado");
    }

    /**
     * Manipula exceções de argumentos inválidos passados para os métodos de
     * serviço.
     * <p>
     * Este handler é disparado quando uma regra de negócio básica (como a
     * obrigatoriedade
     * de ao menos uma tag) é violada antes ou durante o processamento. Ele traduz a
     * {@link IllegalArgumentException} em uma resposta padronizada para o cliente.
     * </p>
     *
     * @param ex A exceção capturada contendo a descrição do erro de validação.
     * @return Um objeto {@link ProblemDetail} com o status HTTP 400 (Bad Request),
     *         informando o motivo específico da rejeição da requisição.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, ex.getMessage(), "Requisição Inválida");
    }

    /**
     * Interceptor de segurança para qualquer exceção não tratada explicitamente.
     * 
     * <p>
     * Evita o vazamento de stacktraces e detalhes de infraestrutura para o cliente,
     * retornando uma mensagem genérica de erro interno.
     * </p>
     * 
     * @param ex A exceção inesperada.
     * @return {@link ProblemDetail} com status 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUncaught(Exception ex) {
        log.error("Erro não tratado.", ex);
        return buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado.", "Erro de Servidor");
    }

    /**
     * Método auxiliar para construção do objeto {@link ProblemDetail}.
     * 
     * <p>
     * Configura os campos base, remove o 'type' (about:blank) e injeta o
     * 'timestamp'.
     * </p>
     * 
     * @param status Código de status HTTP.
     * @param detail Mensagem detalhada.
     * @param title  Título do erro.
     * @return Instância configurada de ProblemDetail.
     */
    private ProblemDetail buildProblemDetail(HttpStatusCode status, String detail, String title) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(null);
        problem.setTitle(title);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
