package ru.mtuci.siscatharsis.repositories.license;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.mtuci.siscatharsis.model.license.License;
import ru.mtuci.siscatharsis.model.license.LicenseHistory;

import java.util.ArrayList;

@Repository
public interface LicenseHistoryRepository extends JpaRepository<LicenseHistory, Long> {
        ArrayList<LicenseHistory> findByLicense(License license);
}
