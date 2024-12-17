package ru.mtuci.siscatharsis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.mtuci.siscatharsis.model.LicenseHistory;

@Repository
public interface LicenseHistoryRepository extends JpaRepository<LicenseHistory, Long> {
}
