package ru.mtuci.siscatharsis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.siscatharsis.model.DeviceLicense;
import ru.mtuci.siscatharsis.model.License;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, Long> {
    Optional<DeviceLicense> findByDeviceIdAndLicenseId(Long deviceId, Long licenseId);
    List<DeviceLicense> getByLicenseId(Long licenseId);
    List<DeviceLicense> getByDeviceId(Long deviceId);
}
