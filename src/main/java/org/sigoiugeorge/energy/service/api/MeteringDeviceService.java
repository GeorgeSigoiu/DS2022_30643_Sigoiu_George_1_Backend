package org.sigoiugeorge.energy.service.api;

import org.sigoiugeorge.energy.model.MeteringDevice;

public interface MeteringDeviceService extends CrudOperationsService<MeteringDevice> {
    Boolean addressIsUnique(String address);

    Boolean deviceExceededMaxConsumption(long deviceId);
}
