package org.sigoiugeorge.energy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "energy_consumption")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnergyConsumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "energy_consumption")
    private Integer energyConsumption;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_metering_device")
    private MeteringDevice meteringDevice;

    @Override
    public String toString() {
        return "EnergyConsumption{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", energyConsumption=" + energyConsumption +
                ", meteringDevice=" + meteringDevice +
                '}';
    }
}
