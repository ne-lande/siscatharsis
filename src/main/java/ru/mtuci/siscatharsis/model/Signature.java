package ru.mtuci.siscatharsis.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "signatures")
public class Signature {
        public enum Status {
                ACTUAL, DELETED, CORRUPTED
        }

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @Column(name = "threat_name")
        private String threatName;

        @Column(name = "first_bytes")
        private Byte firstBytes;

        @Column(name = "remainder_hash")
        private String remainderHash;

        @Column(name = "remainder_length")
        private int remainderLength;

        @Column(name = "file_type")
        private String fileType;

        @Column(name = "offset_start")
        private int offsetStart;

        @Column(name = "offset_end")
        private int offsetEnd;

        @Column(name = "digital_signature", length=1024)
        private String digitalSignature;

        @Column(name = "status")
        private Status status;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private Instant updatedAt;

        @Override
        public String toString() {
                return String.format("%s%s%s%s%s%s%s%s",
                        id,
                        threatName,
                        firstBytes,
                        remainderHash,
                        remainderLength,
                        fileType,
                        offsetStart,
                        offsetEnd);
        }
}
