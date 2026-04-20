package com.techfun.altrua.features.event.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techfun.altrua.core.common.exceptions.DomainException;
import com.techfun.altrua.core.common.exceptions.ResourceNotFoundException;
import com.techfun.altrua.core.common.util.SecurityUtils;
import com.techfun.altrua.features.event.domain.enums.VolunteerStatusEnum;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.event.domain.model.EventVolunteer;
import com.techfun.altrua.features.event.repository.EventRepository;
import com.techfun.altrua.features.event.repository.EventVolunteerRepository;
import com.techfun.altrua.features.user.domain.User;
import com.techfun.altrua.features.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por gerenciar o ciclo de vida das inscrições de
 * voluntários em eventos.
 * <p>
 * Implementa regras de negócio críticas, como a validação de capacidade de
 * eventos e o controle de estados de inscrição, utilizando bloqueios
 * pessimistas para garantir a integridade dos dados em cenários de alta
 * concorrência.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class EventVolunteerService {

    private final EventRepository eventRepository;
    private final EventVolunteerRepository eventVolunteerRepository;
    private final UserRepository userRepository;

    /**
     * Realiza a inscrição ou reativação de um voluntário em um evento específico.
     * <p>
     * O método valida se o usuário já possui inscrição ativa, se o evento pertence
     * à ONG informada e se há vagas disponíveis sob lock de escrita. Caso uma
     * inscrição prévia cancelada seja encontrada, ela é reativada; caso contrário,
     * um novo registro é persistido.
     * </p>
     * 
     * @param eventId Identificador do evento alvo.
     * @param ongId   Identificador da ONG proprietária do evento.
     * @throws DomainException           Se o usuário já estiver inscrito ou se o
     *                                   limite de vagas for atingido.
     * @throws ResourceNotFoundException Se o evento não for localizado no contexto
     *                                   da ONG informada.
     */
    @Transactional
    public void enroll(UUID eventId, UUID ongId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();

        Optional<EventVolunteer> existing = eventVolunteerRepository.findByEventIdAndUserId(eventId, currentUserId);

        if (existing.isPresent() && existing.get().getStatus() == VolunteerStatusEnum.CONFIRMED) {
            throw new DomainException("Você já está inscrito nesse evento");
        }

        Event event = eventRepository.findByIdAndOngIdForUpdate(eventId, ongId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento"));

        long activeCount = eventVolunteerRepository.countByEventIdAndStatus(eventId, VolunteerStatusEnum.CONFIRMED);
        if (!event.acceptsNewVolunteers(activeCount)) {
            throw new DomainException("O evento não aceita mais voluntários.");
        }

        if (existing.isPresent()) {
            EventVolunteer volunteer = existing.get();
            volunteer.reactivate();
            eventVolunteerRepository.save(volunteer);
        } else {
            User user = userRepository.getReferenceById(currentUserId);
            eventVolunteerRepository.save(EventVolunteer.enroll(event, user));
        }
    }

    /**
     * Cancela a inscrição do usuário autenticado em um evento determinado.
     * <p>
     * Utiliza bloqueio pessimista para recuperar a inscrição e validar o vínculo
     * hierárquico com a ONG, garantindo que o cancelamento seja processado de forma
     * isolada e segura. Se a inscrição já estiver cancelada, a operação é encerrada
     * silenciosamente para manter a idempotência.
     * </p>
     * 
     * @param eventId Identificador do evento.
     * @param ongId   Identificador da ONG vinculada ao evento.
     * @throws ResourceNotFoundException Se a inscrição ou o contexto ONG/Evento não
     *                                   forem encontrados.
     */
    @Transactional
    public void cancelEnrollment(UUID eventId, UUID ongId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();

        EventVolunteer volunteer = eventVolunteerRepository
                .findByEventIdAndOngIdAndUserIdForUpdate(eventId, ongId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscrição"));

        if (volunteer.getStatus() == VolunteerStatusEnum.CANCELLED) {
            return;
        }

        volunteer.cancel();
        eventVolunteerRepository.save(volunteer);
    }
}
