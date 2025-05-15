package ru.mtuci.siscatharsis.repositories.signature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.siscatharsis.model.signature.Signature;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, UUID> {
        List<Signature> findAllByUpdatedAtAfter(Instant dateTime);
}
