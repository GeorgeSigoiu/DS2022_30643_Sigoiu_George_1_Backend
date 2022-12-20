package org.sigoiugeorge.energy.service.impl;

import lombok.RequiredArgsConstructor;
import org.sigoiugeorge.energy.dao.MessageRepo;
import org.sigoiugeorge.energy.model.Message;
import org.sigoiugeorge.energy.service.api.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepo repo;

    @Override
    public Message create(Message entity) {
        return repo.save(entity);
    }

    @Override
    public void remove(long id) {
        repo.deleteById(id);
    }

    @Override
    public void remove(Message entity) {
        repo.delete(entity);
    }

    @Override
    public Message get(long id) {
        return repo.findById(id).get();
    }

    @Override
    public List<Message> getAll() {
        return repo.findAll();
    }

    @Override
    public Message update(Message entity) {
        return repo.save(entity);
    }
}
