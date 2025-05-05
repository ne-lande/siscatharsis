package ru.mtuci.siscatharsis.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.model.DeviceLicense;
import ru.mtuci.siscatharsis.model.License;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.repositories.DeviceLicenseRepository;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;

import java.util.List;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class DeviceLicenseService {

    private final DeviceLicenseRepository deviceLicenseRepository;

    public DeviceLicense createDeviceLicense(License license, Device device) {
        DeviceLicense deviceLicense = new DeviceLicense();
        deviceLicense.setDevice(device);
        deviceLicense.setLicense(license);
        deviceLicense.setActivationDate(new Date());

        deviceLicenseRepository.save(deviceLicense);
        return deviceLicense;
    }

    public List<DeviceLicense> getByDeviceId(Long deviceId) {
        return deviceLicenseRepository.getByDeviceId(deviceId);
    }

    public List<DeviceLicense> getByLicenseId(Long licenseId) {
        return deviceLicenseRepository.getByLicenseId(licenseId);
    }

    public DeviceLicense findByDeviceIdAndLicenseId(Long deviceId, Long licenseId) {
        return deviceLicenseRepository.findByDeviceIdAndLicenseId(deviceId, licenseId)
            .orElseThrow(() -> new EntityNotFoundException(
                "DeviceLicense not found by Device and License"
        ));
    }
}
