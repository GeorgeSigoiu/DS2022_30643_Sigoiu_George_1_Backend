package org.sigoiugeorge.energy.dao;

import org.sigoiugeorge.energy.model.EnergyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnergyConsumptionRepo extends JpaRepository<EnergyConsumption, Long> {
}
