package com.techfun.altrua.features.event.domain.enums;

/**
 * Enumeração que representa os possíveis estados de um evento no sistema.
 * 
 * <p>
 * Define o ciclo de vida desde a publicação até o encerramento ou cancelamento,
 * influenciando a visibilidade e as ações permitidas para voluntários e
 * doadores.
 * </p>
 */
public enum EventStatusEnum {
    /**
     * O evento foi criado e está visível para o público.
     */
    PUBLISHED,

    /**
     * O evento está ocorrendo no momento atual de acordo com o cronograma.
     */
    ONGOING,

    /**
     * O evento foi concluído conforme o planejado.
     */
    FINISHED,

    /**
     * O evento foi interrompido ou cancelado pela organização responsável.
     */
    CANCELED;
}
