package org.sigoiugeorge.energy.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.dao.EnergyConsumptionRepo;
import org.sigoiugeorge.energy.model.EnergyConsumption;
import org.sigoiugeorge.energy.service.api.EnergyConsumptionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnergyConsumptionServiceImpl implements EnergyConsumptionService {

    private final EnergyConsumptionRepo repo;

    @Override
    public EnergyConsumption create(@NotNull EnergyConsumption entity) {
        if (entity.getId() != null) {
            throw new IllegalArgumentException("The energy consumption entity exists in database, it has an id!\n" + entity.toString());
        }
        return repo.save(entity);
    }

    @Override
    public void remove(long id) {
        repo.deleteById(id);
    }

    @Override
    public void remove(@NotNull EnergyConsumption entity) {
        remove(entity.getId());
    }

    @Override
    public EnergyConsumption get(long id) {
        return repo.findById(id).get();
    }

    @Override
    public List<EnergyConsumption> getAll() {
        return repo.findAll();
    }

    @Override
    public EnergyConsumption update(@NotNull EnergyConsumption entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("In order to update the energy consumption entity, it needs an id!");
        }
        return repo.save(entity);
    }
}
