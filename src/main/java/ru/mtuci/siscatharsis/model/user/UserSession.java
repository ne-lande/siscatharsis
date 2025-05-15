package ru.mtuci.siscatharsis.model.user;

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

        public enum SessionStatus {
                EXPIRED, ACTIVE, BLOCKED
        }

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        @Column(name = "user_id")
        private Long userId;

        @Column(name = "device_id")
        private Long deviceId;

        @Column(name = "refresh_token")
        private UUID refreshTokenId;

        @Column(name = "status")
        private SessionStatus status;

        @Version
        private Integer version;
}
