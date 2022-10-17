package org.sigoiugeorge.energy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "role")
    private String role;

    @OneToOne
    @JoinColumn(name = "id_credentials", referencedColumnName = "id")
    private Credentials credentials;

    @OneToMany(mappedBy = "user")
    private List<MeteringDevice> meteringDevices;

    public void addMeteringDevice(MeteringDevice device) {
        meteringDevices.add(device);
    }

    public List<MeteringDevice> getMeteringDevices() {
        return List.copyOf(meteringDevices);
    }

    public void removeMeteringDevice(int index) {
        meteringDevices.remove(index);
    }

    public void removeMeteringDevice(MeteringDevice device) {
        meteringDevices.remove(device);
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", credentials=" + credentials +
                ", meteringDevices=" + meteringDevices +
                '}';
    }
}

