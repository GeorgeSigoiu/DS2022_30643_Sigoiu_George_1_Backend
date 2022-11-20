package org.sigoiugeorge.energy.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class EnergyConsumptionResponse {
    private final long deviceId;
    private final LocalDateTime timestamp;
    private final Double currentEnergyValue;

    @JsonCreator
    public EnergyConsumptionResponse(
            @JsonProperty("id") long deviceId,
            @JsonProperty("timestamp") LocalDateTime timestamp,
            @JsonProperty("value") Double currentEnergyValue
    ) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.currentEnergyValue = currentEnergyValue;
    }
}