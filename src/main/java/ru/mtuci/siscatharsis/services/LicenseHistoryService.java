package ru.mtuci.siscatharsis.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.model.License;
import ru.mtuci.siscatharsis.model.LicenseHistory;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.repositories.LicenseHistoryRepository;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class LicenseHistoryService {

        private final LicenseHistoryRepository licenseHistoryRepository;

        public LicenseHistory findForLicenseByCode(Long licenseId) {
                return null;
        }

        private LicenseHistory create(License license, User issuer) {
                LicenseHistory licenseHistory = LicenseHistory.builder()
                        .license(license)
                        .user(issuer)
                        .changeDate(new Date())
                        .build();

                return licenseHistory;
        }

        // Admin actions
        public LicenseHistory adminCreate(License license, User issuer) {
                LicenseHistory licenseHistory = create(license, issuer);

                licenseHistory.setStatus("Created");
                licenseHistory.setDescription("New license was created");

                licenseHistoryRepository.save(licenseHistory);

                return licenseHistory;
        }

        public LicenseHistory adminUpdate(License license, User issuer) {
                LicenseHistory licenseHistory = create(license, issuer);

                licenseHistory.setStatus("Updated");
                licenseHistory.setDescription("License was updated");

                licenseHistoryRepository.save(licenseHistory);

                return licenseHistory;
        }

        // User actions
        public LicenseHistory userActivate(License license, User issuer) {
                LicenseHistory licenseHistory = create(license, issuer);

                licenseHistory.setStatus("Activated");
                licenseHistory.setDescription("Owner has activated the license");

                licenseHistoryRepository.save(licenseHistory);

                return licenseHistory;
        }

        public LicenseHistory userUpdate(License license, User issuer) {
                LicenseHistory licenseHistory = create(license, issuer);

                licenseHistory.setStatus("Updated");
                licenseHistory.setDescription("Owner has updated the license");

                licenseHistoryRepository.save(licenseHistory);

                return licenseHistory;

        }
}
