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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.techfun.altrua.core.common.exceptions.BusinessException;
import com.techfun.altrua.core.common.exceptions.DuplicateResourceException;
import com.techfun.altrua.core.common.exceptions.InvalidCredentialsException;

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
    public ProblemDetail handleAuthentication(Exception ex) {
        return buildProblemDetail(HttpStatus.UNAUTHORIZED, "Credenciais inválidas", "Falha na Autenticação");
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
     * Manipula exceções de acesso negado quando um usuário autenticado tenta
     * realizar
     * uma operação para a qual não possui permissões suficientes.
     * <p>
     * Este método intercepta a {@link AccessDeniedException} lançada pela camada de
     * serviço
     * ou pelo Spring Security e a traduz em um formato de erro padronizado (RFC
     * 7807).
     * </p>
     *
     * @param ex A exceção de acesso negado capturada, contendo a mensagem de erro
     *           específica.
     * @return Um objeto {@link ProblemDetail} com o status HTTP 403 (Forbidden),
     *         detalhando a violação de segurança para o cliente.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        return buildProblemDetail(HttpStatus.FORBIDDEN, ex.getMessage(), "Acesso Negado");
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
