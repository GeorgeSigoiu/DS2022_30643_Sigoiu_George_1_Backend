package org.sigoiugeorge.energy.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class EnergyConsumptionShort {
    private LocalDate date;
    private Integer value;
    private Integer hour;
    private long deviceId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnergyConsumptionShort that = (EnergyConsumptionShort) o;
        return this.deviceId == that.deviceId && this.date.equals(that.date) && this.hour.equals(that.hour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, hour, deviceId);
    }

}
