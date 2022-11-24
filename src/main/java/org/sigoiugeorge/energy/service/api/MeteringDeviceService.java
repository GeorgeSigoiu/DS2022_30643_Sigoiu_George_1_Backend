package org.sigoiugeorge.energy.service.api;

import org.sigoiugeorge.energy.model.MeteringDevice;

import java.time.LocalDateTime;

public interface MeteringDeviceService extends CrudOperationsService<MeteringDevice> {
    Boolean addressIsUnique(String address);

    Boolean deviceExceededMaxHourlyConsumption(long deviceId, LocalDateTime timestamp);
}
