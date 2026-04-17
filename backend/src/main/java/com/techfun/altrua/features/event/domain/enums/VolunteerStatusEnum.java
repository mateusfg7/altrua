package com.techfun.altrua.features.event.domain.enums;

/**
 * Define os possíveis estados de uma inscrição de voluntário no sistema.
 * <p>
 * CONFIRMED indica que a inscrição está ativa e contabiliza para o limite de
 * vagas do evento, enquanto CANCELLED representa a desistência ou remoção do
 * voluntário, liberando a vaga para novos ingressos.
 * </p>
 */
public enum VolunteerStatusEnum {
    /** Inscrição ativa e válida para participação no evento. */
    CONFIRMED,

    /** Inscrição inativa após solicitação de cancelamento. */
    CANCELLED
}
