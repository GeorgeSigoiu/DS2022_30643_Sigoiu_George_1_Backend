package org.sigoiugeorge.energy.dao;

import org.sigoiugeorge.energy.model.MeteringDevice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeteringDeviceRepo extends JpaRepository<MeteringDevice, Long> {
}
