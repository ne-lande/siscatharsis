package ru.mtuci.siscatharsis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.siscatharsis.model.SignatureAudit;

public interface SignatureAuditRepository extends JpaRepository<SignatureAudit, Long> {
}
