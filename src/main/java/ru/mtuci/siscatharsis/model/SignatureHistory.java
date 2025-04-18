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
@Table(name = "signature_history")
public class SignatureHistory {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        @Column(name = "history_id")
        private Long historyId;

        @Column(name = "signature_id")
        private UUID signatureId;

        @UpdateTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "version_created_at")
        private Instant versionCreatedAt;

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
        private Signature.Status status;

        @Column(name = "updated_at")
        @Temporal(TemporalType.TIMESTAMP)
        private Instant updatedAt;

        public static SignatureHistory fromSignature(Signature signature) {
                SignatureHistory history = SignatureHistory.builder()
                        .signatureId(signature.getId())
                        .threatName(signature.getThreatName())
                        .firstBytes(signature.getFirstBytes())
                        .remainderHash(signature.getRemainderHash())
                        .remainderLength(signature.getRemainderLength())
                        .fileType(signature.getFileType())
                        .offsetStart(signature.getOffsetStart())
                        .offsetEnd(signature.getOffsetEnd())
                        .digitalSignature(signature.getDigitalSignature())
                        .status(signature.getStatus())
                        .updatedAt(signature.getUpdatedAt())
                        .build();

                return history;
        }
}
