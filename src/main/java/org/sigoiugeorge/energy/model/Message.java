package org.sigoiugeorge.energy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "id_topic")
    private Ticket ticket;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "username")
    private String username;

    @Column(name = "read_1")
    private Integer read ;

    public void setTimestampNow() {
        timestamp = LocalDateTime.now();
    }


    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                ", userType='" + userType + '\'' +
                ", username='" + username + '\'' +
                ", read=" + read +
                '}';
    }
}
