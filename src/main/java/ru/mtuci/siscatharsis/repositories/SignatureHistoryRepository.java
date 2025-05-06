package ru.mtuci.siscatharsis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.siscatharsis.model.SignatureHistory;

public interface SignatureHistoryRepository extends JpaRepository<SignatureHistory, Long>  {
}
