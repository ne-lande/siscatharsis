package ru.mtuci.siscatharsis.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SessionId implements Serializable {
        private static final long serialVersionUID = 1L;

        @Column(name = "user_id")
        private Long userId;

        @Column(name = "device_id")
        private Long deviceId;

        // Getters, Setters, hashCode, equals
        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                SessionId that = (SessionId) o;
                return Objects.equals(userId, that.userId) &&
                        Objects.equals(deviceId, that.deviceId);
        }

        @Override
        public int hashCode() {
                return Objects.hash(userId, deviceId);
        }
}