package org.sigoiugeorge.energy.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.dao.CredentialsRepo;
import org.sigoiugeorge.energy.model.Credentials;
import org.sigoiugeorge.energy.service.api.CredentialsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CredentialsServiceImpl implements CredentialsService {

    private final CredentialsRepo repo;

    @Override
    public Credentials create(@NotNull Credentials entity) {
        if (entity.getId() != null) {
            throw new IllegalArgumentException("The credentials exist in database, they have an id!\n" + entity.toString());
        }
        return repo.save(entity);
    }

    @Override
    public void remove(long id) {
        repo.deleteById(id);
    }

    @Override
    public void remove(@NotNull Credentials entity) {
        remove(entity.getId());
    }

    @Override
    public Credentials get(long id) {
        return repo.findById(id).get();
    }

    @Override
    public List<Credentials> getAll() {
        return repo.findAll();
    }

    @Override
    public Credentials update(@NotNull Credentials entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("In order to update the credentials, the ID can not be null!");
        }
        return repo.save(entity);
    }

    @Override
    public Boolean usernameIsUnique(String username) {
        Optional<Credentials> byUsername = repo.findByUsername(username);
        return byUsername.isEmpty();
    }
}
