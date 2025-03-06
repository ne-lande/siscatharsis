package ru.mtuci.siscatharsis.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_sessions")
public class UserSession {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;
        /*
        @EmbeddedId
        private SessionId id = new SessionId();
         */

        @ManyToOne(cascade = CascadeType.ALL)
        @JoinColumn(name = "user_id")
        @JsonBackReference
        private User user;

        @ManyToOne(cascade = CascadeType.ALL)
        @JoinColumn(name = "device_id")
        @JsonBackReference
        private Device device;

        @Column(name = "refresh_token_uuid")
        private UUID refreshTokenId;
}
