package org.sigoiugeorge.energy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "topic")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username_client")
    private String clientUsername;

    @Column(name = "username_admin")
    private String adminUsername;

    @Column(name = "status")
    private String status;

    @Column(name = "created")
    private LocalDateTime created;

    @OneToMany(mappedBy = "ticket", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<Message> messages;
}
