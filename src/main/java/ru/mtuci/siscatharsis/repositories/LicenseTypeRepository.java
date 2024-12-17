package ru.mtuci.siscatharsis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.siscatharsis.model.LicenseType;

@Repository
public interface LicenseTypeRepository extends JpaRepository<LicenseType, Long> {
}
