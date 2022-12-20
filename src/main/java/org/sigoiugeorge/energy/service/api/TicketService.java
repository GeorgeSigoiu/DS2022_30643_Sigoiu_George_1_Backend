package org.sigoiugeorge.energy.service.api;

import org.sigoiugeorge.energy.model.Ticket;

import java.util.Optional;

public interface TicketService extends CrudOperationsService<Ticket> {
    Optional<Ticket> findOpenTopicForClient(String username);
    Optional<Ticket> findOpenOrSolvedTopicForClient(String username);
    Optional<Ticket> findSolvedTopicForClient(String username);
    Ticket getUnassignedTopic();
}
