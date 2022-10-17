package org.sigoiugeorge.energy.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.dao.MeteringDeviceRepo;
import org.sigoiugeorge.energy.model.EnergyConsumption;
import org.sigoiugeorge.energy.model.MeteringDevice;
import org.sigoiugeorge.energy.service.api.MeteringDeviceService;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MeteringDeviceServiceImpl implements MeteringDeviceService {

    private final MeteringDeviceRepo repo;

    @Override
    public MeteringDevice save(@NotNull MeteringDevice entity) {
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
    public void remove(MeteringDevice entity) {
        remove(entity.getId());
    }

    @Override
    public MeteringDevice get(long id) {
        return repo.getReferenceById(id);
    }

    @Override
    public MeteringDevice update(@NotNull MeteringDevice entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("The device can not be updated because it does not have an id!");
        }
        return repo.save(entity);
    }

    @Override
    public @NotNull List<EnergyConsumption> getAllEnergyConsumptions(@NotNull MeteringDevice device) {
        return device.getEnergyConsumption();
    }

    @Override
    public @NotNull List<EnergyConsumption> getAllEnergyConsumptions(long deviceId) {
        MeteringDevice device = get(deviceId);
        return getAllEnergyConsumptions(device);
    }

    @Override
    public void addEnergyConsumption(@NotNull MeteringDevice device, @NotNull EnergyConsumption energyConsumption) {
        device.addEnergyConsumption(energyConsumption);
        save(device);
    }

    @Override
    public void addEnergyConsumption(long deviceId, @NotNull EnergyConsumption energyConsumption) {
        MeteringDevice device = get(deviceId);
        addEnergyConsumption(device, energyConsumption);
    }

    @Override
    public void removeEnergyConsumption(@NotNull MeteringDevice device, long energyId) {
        List<EnergyConsumption> list = device.getEnergyConsumption();
        int index = -1;
        boolean found = false;
        for (EnergyConsumption en : list) {
            index++;
            if (en.getId() == energyId) {
                found = true;
                break;
            }
        }
        if (found) {
            device.removeEnergyConsumption(index);
            save(device);
        }
    }

    @Override
    public void removeEnergyConsumption(@NotNull MeteringDevice device, @NotNull EnergyConsumption energyConsumption) {
        device.removeEnergyConsumption(energyConsumption);
        save(device);
    }

    @Override
    public void removeEnergyConsumption(long deviceId, long energyId) {
        MeteringDevice device = get(deviceId);
        removeEnergyConsumption(device, energyId);
    }

    @Override
    public void removeEnergyConsumption(long deviceId, @NotNull EnergyConsumption energyConsumption) {
        MeteringDevice device = get(deviceId);
        removeEnergyConsumption(device, energyConsumption);
    }
}
