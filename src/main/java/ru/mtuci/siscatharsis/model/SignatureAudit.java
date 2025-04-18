package ru.mtuci.siscatharsis.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "signatures_audit")
public class SignatureAudit {

        public enum ChangeType {
               CREATED, UPDATED, DELETED, CORRUPTED
        }

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        @Column(name = "audit_id")
        private Long auditId;

        @Column(name = "signature_id")
        private UUID signatureId;

        @Column(name = "changed_by")
        private Long changedBy;

        @Column(name = "change_type")
        private ChangeType changeType;

        @UpdateTimestamp
        @Column(name = "changed_at")
        private Instant changedAt;

        @Column(name = "fields_changed", length=1024)
        private String fieldsChanged;
}
