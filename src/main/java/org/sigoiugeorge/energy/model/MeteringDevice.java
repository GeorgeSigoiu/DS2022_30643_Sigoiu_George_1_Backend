package org.sigoiugeorge.energy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "metering_device")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeteringDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "address")
    private String address;

    @Column(name = "max_hourly_energy_consumption")
    private Integer maxHourlyEnergyConsumption;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    private User user;

    @OneToMany(mappedBy = "meteringDevice", fetch = FetchType.EAGER)
    private Set<EnergyConsumption> energyConsumption;

    public Set<EnergyConsumption> getEnergyConsumption() {
        return energyConsumption;
    }

}
