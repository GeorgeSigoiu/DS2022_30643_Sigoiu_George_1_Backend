package org.sigoiugeorge.energy.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.dao.EnergyConsumptionRepo;
import org.sigoiugeorge.energy.dao.MeteringDeviceRepo;
import org.sigoiugeorge.energy.model.EnergyConsumption;
import org.sigoiugeorge.energy.model.MeteringDevice;
import org.sigoiugeorge.energy.service.api.EnergyConsumptionService;
import org.sigoiugeorge.energy.utils.EnergyConsumptionResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnergyConsumptionServiceImpl implements EnergyConsumptionService {

    private final EnergyConsumptionRepo repo;
    private final MeteringDeviceRepo deviceRepo;

    @Override
    public EnergyConsumption create(@NotNull EnergyConsumption entity) {
        if (entity.getId() != null) {
            throw new IllegalArgumentException("The energy consumption entity exists in database, it has an id!\n" + entity);
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
        if (repo.findById(id).isEmpty()) {
            throw new RuntimeException("Consumption with id=" + id + " does not exist!");
        }
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

    @Override
    public void addEnergyConsumption(@NotNull EnergyConsumptionResponse consumer) {
        long deviceId = consumer.getDeviceId();
        Optional<MeteringDevice> deviceById = deviceRepo.findById(deviceId);
        if (deviceById.isEmpty()) {
            throw new RuntimeException("Device(id=" + deviceId + ") does not exist!");
        }
        MeteringDevice device = deviceById.get();
        EnergyConsumption energyConsumption = new EnergyConsumption();
        energyConsumption.setMeteringDevice(device);
        //todo from int to double
        energyConsumption.setEnergyConsumption(consumer.getCurrentEnergyValue().intValue());
        energyConsumption.setTimestamp(consumer.getTimestamp());
        repo.save(energyConsumption);
    }
}
