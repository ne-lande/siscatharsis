package ru.mtuci.siscatharsis.repositories.signature;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.siscatharsis.model.signature.SignatureAudit;

import java.util.List;
import java.util.UUID;

public interface SignatureAuditRepository extends JpaRepository<SignatureAudit, Long> {
        List<SignatureAudit> findBySignatureId(UUID signatureId);
}
