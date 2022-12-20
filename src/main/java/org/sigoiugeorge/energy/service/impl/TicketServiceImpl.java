package org.sigoiugeorge.energy.service.impl;

import lombok.RequiredArgsConstructor;
import org.sigoiugeorge.energy.dao.TicketRepo;
import org.sigoiugeorge.energy.model.Ticket;
import org.sigoiugeorge.energy.service.api.TicketService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepo repo;

    @Override
    public Ticket create(Ticket entity) {
        if (entity.getCreated() == null) {
            entity.setCreated(LocalDateTime.now());
        }
        if (entity.getId() != null) {
            throw new IllegalArgumentException("The topic exists in database, they have an id!\n" + entity.toString());
        }
        return repo.save(entity);
    }

    @Override
    public void remove(long id) {
        repo.deleteById(id);
    }

    @Override
    public void remove(Ticket entity) {
        repo.save(entity);
    }

    @Override
    public Ticket get(long id) {
        return repo.findById(id).get();
    }

    @Override
    public List<Ticket> getAll() {
        return repo.findAll();
    }

    @Override
    public Ticket update(Ticket entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("In order to update the topic, the ID can not be null!");
        }
        return repo.save(entity);
    }

    @Override
    public Optional<Ticket> findOpenTopicForClient(String username) {
        List<Ticket> all = repo.findAll().stream().filter(t -> t.getClientUsername().equals(username)).toList();
        return all.stream().filter(t -> t.getStatus().equals("open")).findAny();
    }

    @Override
    public Optional<Ticket> findOpenOrSolvedTopicForClient(String username) {
        List<Ticket> all = repo.findAll().stream().filter(t -> t.getClientUsername().equals(username)).toList();
        return all.stream().filter(t -> t.getStatus().equals("open") || t.getStatus().equals("solved")).findAny();
    }

    @Override
    public Optional<Ticket> findSolvedTopicForClient(String username) {
        List<Ticket> all = repo.findAll().stream().filter(t -> t.getClientUsername().equals(username)).toList();
        return all.stream().filter(t -> t.getStatus().equals("solved")).findAny();
    }

    @Override
    public Ticket getUnassignedTopic() {
        List<Ticket> tickets = new java.util.ArrayList<>(
                repo.findAll().stream()
                        .filter(t -> t.getAdminUsername() == null && t.getStatus().equals("open"))
                        .toList()
        );
        tickets.sort(Comparator.comparing(Ticket::getCreated));
        return tickets.get(0);
    }
}
