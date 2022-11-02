package org.sigoiugeorge.energy.dao;

import org.sigoiugeorge.energy.model.MeteringDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeteringDeviceRepo extends JpaRepository<MeteringDevice, Long> {
    Optional<MeteringDevice> findByAddress(String address);
}
