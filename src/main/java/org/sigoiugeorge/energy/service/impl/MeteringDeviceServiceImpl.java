package org.sigoiugeorge.energy.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.dao.MeteringDeviceRepo;
import org.sigoiugeorge.energy.model.EnergyConsumption;
import org.sigoiugeorge.energy.model.MeteringDevice;
import org.sigoiugeorge.energy.service.api.MeteringDeviceService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MeteringDeviceServiceImpl implements MeteringDeviceService {

    private final MeteringDeviceRepo repo;

    @Override
    public MeteringDevice create(@NotNull MeteringDevice entity) {
        if (entity.getId() != null) {
            throw new IllegalArgumentException("The device exists in database, it has an id!\n" + entity.toString());
        }
        return repo.save(entity);
    }

    @Override
    public void remove(long id) {
        repo.deleteById(id);
    }

    @Override
    public void remove(@NotNull MeteringDevice entity) {
        remove(entity.getId());
    }

    @Override
    public MeteringDevice get(long id) {
        if (repo.findById(id).isEmpty()) {
            throw new RuntimeException("Device with id=" + id + " does not exist!");
        }
        return repo.findById(id).get();
    }

    @Override
    public List<MeteringDevice> getAll() {
        return repo.findAll();
    }

    @Override
    public MeteringDevice update(@NotNull MeteringDevice entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("The device can not be updated because it does not have an id!");
        }
        return repo.save(entity);
    }

    @Override
    public Boolean addressIsUnique(String address) {
        Optional<MeteringDevice> byAddress = repo.findByAddress(address);
        return byAddress.isEmpty();
    }

    @Override
    public Boolean deviceExceededMaxConsumption(long deviceId) {
        Optional<MeteringDevice> byId = repo.findById(deviceId);
        if (byId.isEmpty()) {
            throw new RuntimeException("Device with id=" + deviceId + " does not exist!");
        }
        MeteringDevice meteringDevice = byId.get();
        List<EnergyConsumption> energyConsumption = new ArrayList<>(meteringDevice.getEnergyConsumption());
        if (energyConsumption == null) {
            return false;
        }
        if (energyConsumption.size() < 1) {
            return false;
        }
        energyConsumption.sort((a, b) -> a.getEnergyConsumption().compareTo(b.getEnergyConsumption()));
        EnergyConsumption lastConsumption = energyConsumption.get(energyConsumption.size() - 1);
        Integer currentValue = lastConsumption.getEnergyConsumption();
        return currentValue > meteringDevice.getMaxHourlyEnergyConsumption();
    }

}
