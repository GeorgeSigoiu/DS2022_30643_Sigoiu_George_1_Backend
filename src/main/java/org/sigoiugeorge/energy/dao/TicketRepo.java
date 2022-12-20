package org.sigoiugeorge.energy.dao;

import org.sigoiugeorge.energy.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepo extends JpaRepository<Ticket, Long> {
}
