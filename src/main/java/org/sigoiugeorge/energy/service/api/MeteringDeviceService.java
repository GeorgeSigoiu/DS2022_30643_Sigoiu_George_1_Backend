package org.sigoiugeorge.energy.service.api;

import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.model.EnergyConsumption;
import org.sigoiugeorge.energy.model.MeteringDevice;

import java.util.List;

public interface MeteringDeviceService extends CrudOperationsService<MeteringDevice> {
    @NotNull
    List<EnergyConsumption> getAllEnergyConsumptions(@NotNull MeteringDevice device);

    @NotNull
    List<EnergyConsumption> getAllEnergyConsumptions(long deviceId);

    void addEnergyConsumption(@NotNull MeteringDevice device, @NotNull EnergyConsumption energyConsumption);

    void addEnergyConsumption(long deviceId, @NotNull EnergyConsumption energyConsumption);

    void removeEnergyConsumption(@NotNull MeteringDevice device, long energyId);

    void removeEnergyConsumption(@NotNull MeteringDevice device, @NotNull EnergyConsumption energyConsumption);

    void removeEnergyConsumption(long deviceId, long energyId);

    void removeEnergyConsumption(long deviceId, @NotNull EnergyConsumption energyConsumption);

    Boolean addressIsUnique(String address);

    Boolean deviceExceededMaxConsumption(long deviceId);
}
