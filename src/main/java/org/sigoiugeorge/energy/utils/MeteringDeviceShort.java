package org.sigoiugeorge.energy.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeteringDeviceShort {
    @JsonProperty("address")
    private String address;
    @JsonProperty("max_consumption")
    private Integer maxConsumption;

    public MeteringDeviceShort(String address, Integer maxHourlyEnergyConsumption) {
        this.address = address;
        this.maxConsumption = maxHourlyEnergyConsumption;
    }
}
