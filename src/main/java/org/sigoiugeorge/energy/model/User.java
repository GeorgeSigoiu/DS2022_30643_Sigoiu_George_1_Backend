package org.sigoiugeorge.energy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "role")
    private String role;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_credentials", referencedColumnName = "id")
    private Credentials credentials;

    @JsonProperty("devices")
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<MeteringDevice> meteringDevices = new HashSet<>();

    public void addMeteringDevice(MeteringDevice device) {
        meteringDevices.add(device);
    }

    public Set<MeteringDevice> getMeteringDevices() {
        return meteringDevices;
    }

}

