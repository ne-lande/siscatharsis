package ru.mtuci.siscatharsis.services.license;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.model.license.License;
import ru.mtuci.siscatharsis.model.license.LicenseHistory;
import ru.mtuci.siscatharsis.model.user.User;
import ru.mtuci.siscatharsis.repositories.license.LicenseHistoryRepository;

import java.util.ArrayList;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class LicenseHistoryService {

        private final LicenseHistoryRepository licenseHistoryRepository;

        public ArrayList<LicenseHistory> findForLicense(License license) {
                return licenseHistoryRepository.findByLicense(license);
        }

        @SuppressWarnings("UnusedReturnValue")
        public LicenseHistory create(License license, User issuer, LicenseHistory.ChangeType changeType) {
                LicenseHistory licenseHistory = LicenseHistory.builder()
                        .license(license)
                        .user(issuer)
                        .changeType(changeType)
                        .changeDate(new Date())
                        .build();

                licenseHistoryRepository.save(licenseHistory);

                return licenseHistory;
        }
}
