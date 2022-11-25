package org.sigoiugeorge.energy.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class TextMessageDTO {

    @JsonProperty("message_exceeded_consumption")
    private final String devicesExceededHourlyConsumption;
    @JsonProperty("device_consumption")
    private final String newConsumptionForDevice;
}