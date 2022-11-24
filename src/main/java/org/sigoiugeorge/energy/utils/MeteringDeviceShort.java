package org.sigoiugeorge.energy.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class MeteringDeviceShort {
    @JsonProperty("address")
    private String address;
    @JsonProperty("max_hourly_consumption")
    private Integer maxHourlyConsumption;
    @JsonProperty("date")
    private LocalDate date;
    @JsonProperty("time")
    private Integer time;

}
