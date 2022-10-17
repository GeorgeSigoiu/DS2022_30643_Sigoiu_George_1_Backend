package org.sigoiugeorge.energy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "metering_device")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeteringDevice {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "address")
    private String address;

    @Column(name = "max_hourly_energy_consumption")
    private Integer maxHourlyEnergyConsumption;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @OneToMany(mappedBy = "meteringDevice")
    private List<EnergyConsumption> energyConsumption;

    public void addEnergyConsumption(EnergyConsumption en) {
        energyConsumption.add(en);
    }

    public List<EnergyConsumption> getEnergyConsumption() {
        return List.copyOf(energyConsumption);
    }

    public void removeEnergyConsumption(int index) {
        energyConsumption.remove(index);
    }

    public void removeEnergyConsumption(@NotNull EnergyConsumption en) {
        energyConsumption.remove(en);
    }

    @Override
    public String toString() {
        return "MeteringDevice{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", maxHourlyEnergyConsumption=" + maxHourlyEnergyConsumption +
                ", user=" + user +
                ", energyConsumption=" + energyConsumption +
                '}';
    }
}
