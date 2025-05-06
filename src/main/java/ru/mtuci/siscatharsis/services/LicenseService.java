package ru.mtuci.siscatharsis.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.mtuci.siscatharsis.model.*;
import ru.mtuci.siscatharsis.repositories.LicenseRepository;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;
import ru.mtuci.siscatharsis.utils.LicenseException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final LicenseHistoryService licenseHistoryService;
    private final DeviceLicenseService deviceLicenseService;

    public License requireById(Long id) {
        return licenseRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("License not found")
        );
    }

    public License requireByCode(UUID code) {
        return licenseRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException("License not found with the given activation code")
        );
    }

    public License create(License license, User issuer) {
        licenseRepository.save(license);

        licenseHistoryService.adminCreate(license, issuer);

        return license;
    }

    public License update(Long id, License updateLicense, User issuer) {
        License license = requireById(id);

        license.setUser(updateLicense.getUser());
        license.setProduct(updateLicense.getProduct());
        license.setType(updateLicense.getType());

        license.setFirstActivationDate(updateLicense.getFirstActivationDate());
        license.setEndingDate(updateLicense.getEndingDate());

        license.setIsBlocked(updateLicense.getIsBlocked());
        license.setDevicesCount(updateLicense.getDevicesCount());
        license.setDuration(updateLicense.getDuration());
        license.setDescription(updateLicense.getDescription());

        licenseRepository.save(license);

        licenseHistoryService.adminUpdate(license, issuer);

        return license;
    }

    public List<License> getActiveLicensesForDevice(Device device) {
        List<DeviceLicense> deviceLicenses = deviceLicenseService.getByDeviceId(device.getId());
        List<License> activeLicenses = new ArrayList<>();

        for (DeviceLicense deviceLicense : deviceLicenses) {
            Long licenseId = deviceLicense.getLicense().getId();

            License license = licenseRepository.findById(licenseId).orElse(null);

            if (license == null || license.getIsBlocked()) {
                continue;
            }

            activeLicenses.add(license);
        }

        return activeLicenses;
    }

    public License activateLicense(UUID activationCode, Device device, User user) throws Exception {
        License license = requireByCode(activationCode);

        validateActivation(license, device, user);

        if (license.getFirstActivationDate() == null) {
            updateLicenseForActivation(license, user);
        }

        deviceLicenseService.createDeviceLicense(license, device);

        licenseHistoryService.userActivate(license, user);

        return license;
    }
    
    public License renewExistentLicense(UUID licenseCode, User user, Device device) throws Exception {
        License license = requireByCode(licenseCode);

        validateRenewal(license);

        license.setEndingDate(
                new Date(license.getEndingDate().getTime() + license.getDuration())
        );

        licenseRepository.save(license);

        licenseHistoryService.userUpdate(license, user);

        return license;
    }

    private void validateRenewal(License license) throws LicenseException{
        if (license.getIsBlocked()) {
            throw new LicenseException("License is blocked");
        }

        if (license.getFirstActivationDate() == null) {
            throw new LicenseException("License is not activated");
        }

    }
    private void validateActivation(License license, Device device, User user) throws LicenseException {
        if (license.getUser() != null && !license.getUser().equals(user)) {
            throw new LicenseException("License already activated by another user");
        }

        if (license.getIsBlocked()) {
            throw new LicenseException("License is blocked");
        }

        if (license.getEndingDate() != null && license.getEndingDate().before(new Date())) {
            throw new LicenseException("License is expired");
        }

        int devicesCount = deviceLicenseService.getByLicenseId(license.getId()).size();
        if (license.getDevicesCount() <= devicesCount) {
            throw new LicenseException("Device count exceeded");
        }
    }

    private void updateLicenseForActivation(License license, User user) {
        // не нужно вычислять сегодняшее время два раза, лучше по референсу
        Date activationDate = new Date();
        Date endingDate = new Date(activationDate.getTime() + license.getDuration());

        license.setFirstActivationDate(activationDate);
        license.setEndingDate(endingDate);
        license.setUser(user);

        licenseRepository.save(license);
    }

    public void delete(Long id) {
        License license = requireById(id);

        licenseRepository.delete(license);
    }
}
