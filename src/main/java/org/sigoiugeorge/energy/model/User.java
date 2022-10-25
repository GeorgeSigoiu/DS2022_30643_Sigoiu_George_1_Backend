package org.sigoiugeorge.energy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    private List<MeteringDevice> meteringDevices = new ArrayList<>();

    public void addMeteringDevice(MeteringDevice device) {
        meteringDevices.add(device);
    }

    public List<MeteringDevice> getMeteringDevices() {
        return meteringDevices;
    }

    public void removeMeteringDevice(int index) {
        meteringDevices.remove(index);
    }

    public void removeMeteringDevice(MeteringDevice device) {
        meteringDevices.remove(device);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", credentials=" + credentials +
                ", meteringDevices=" + meteringDevices +
                '}';
    }
}

