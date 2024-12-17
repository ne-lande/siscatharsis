package ru.mtuci.siscatharsis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.model.DeviceLicense;
import ru.mtuci.siscatharsis.model.License;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.dto.internal.DeviceLicenseRequest;
import ru.mtuci.siscatharsis.repositories.DeviceLicenseRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDService;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;

import java.util.List;
import java.util.Date;

@Service
public class DeviceLicenseService extends AbstractCRUDService<DeviceLicense, DeviceLicenseRequest, DeviceLicenseRepository> {

    @Autowired
    public DeviceLicenseService(DeviceLicenseRepository repository) {
        super(repository, DeviceLicense.class);
    }

    @Override
    public DeviceLicense create(DeviceLicenseRequest deviceLicenseRequest) {
        return null;
    }

    @Override
    public DeviceLicense update(Long id, DeviceLicenseRequest deviceLicenseRequest) {
        return null;
    }

    public DeviceLicense createDeviceLicense(License license, Device device) {
        DeviceLicense deviceLicense = new DeviceLicense();
        deviceLicense.setDevice(device);
        deviceLicense.setLicense(license);
        deviceLicense.setActivationDate(new Date());

        this.save(deviceLicense);
        return deviceLicense;
    }

    public List<DeviceLicense> findByLicense(License license) {
        return repository.findByLicense(license);
    }

    public DeviceLicense findByDeviceIdAndLicenseId(Long deviceId, Long licenseId) {
        return repository.findByDeviceIdAndLicenseId(deviceId, licenseId)
            .orElseThrow(() -> new EntityNotFoundException(
                "DeviceLicense not found by Device and License"
        ));
    }
}
