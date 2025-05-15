package ru.mtuci.siscatharsis.repositories.signature;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.siscatharsis.model.signature.SignatureHistory;

import java.util.List;
import java.util.UUID;

public interface SignatureHistoryRepository extends JpaRepository<SignatureHistory, Long>  {
        List<SignatureHistory> findBySignatureId(UUID signatureId);
}
