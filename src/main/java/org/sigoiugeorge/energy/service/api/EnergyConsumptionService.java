package org.sigoiugeorge.energy.service.api;

import org.sigoiugeorge.energy.model.EnergyConsumption;
import org.sigoiugeorge.energy.utils.EnergyConsumptionResponse;


public interface EnergyConsumptionService extends CrudOperationsService<EnergyConsumption> {
    void addEnergyConsumption(EnergyConsumptionResponse consumer);
}
