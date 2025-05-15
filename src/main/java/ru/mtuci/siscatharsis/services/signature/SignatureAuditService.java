package ru.mtuci.siscatharsis.services.signature;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.model.signature.Signature;
import ru.mtuci.siscatharsis.model.signature.SignatureAudit;
import ru.mtuci.siscatharsis.repositories.signature.SignatureAuditRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignatureAuditService {
        private final SignatureAuditRepository signatureAuditRepository;

        public List<SignatureAudit> getFor(Signature signature) {
                return signatureAuditRepository.findBySignatureId(signature.getId());
        }

        @SuppressWarnings("UnusedReturnValue")
        public SignatureAudit create(UUID signatureId, Long issuerId, SignatureAudit.ChangeType changeType, List<String> changedFields) {
                SignatureAudit audit = SignatureAudit.builder()
                        .signatureId(signatureId)
                        .changedBy(issuerId)
                        .changeType(changeType)
                        .changedAt(Instant.now())
                        .fieldsChanged(changedFields)
                        .build();

                signatureAuditRepository.save(audit);

                return audit;
        }
}
