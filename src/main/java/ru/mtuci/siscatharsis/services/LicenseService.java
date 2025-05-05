package ru.mtuci.siscatharsis.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.mtuci.siscatharsis.dto.external.license.response.Ticket;
import ru.mtuci.siscatharsis.model.*;
import ru.mtuci.siscatharsis.repositories.LicenseRepository;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;
import ru.mtuci.siscatharsis.utils.LicenseException;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

// Я переместил тудушки туда где они исправлены

//TODO: 1. Нужен рефакторинг. Много неиспользуемых переменных
//TODO: 3. generateLicenseResponse - получается, что лицензия в тикете всегда разблокирована
//TODO: 4. generateLicenseCode - подпись должна быть не просто хэшем. Нужно генерировать открытый ключ, чтобы клиент мог её проверить
//TODO: 5. activateLicense - пересмотреть проверку. Пользователь должен иметь возможность повторно активировать лицензию на другом устройстве

@Service
@RequiredArgsConstructor
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final LicenseHistoryService licenseHistoryService;
    private final DeviceLicenseService deviceLicenseService;
    private final CryptoService cryptoService;

    public License requireById(Long id) {
        return licenseRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("License not found")
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

    //TODO: 5. activateLicense - пересмотреть проверку. Пользователь должен иметь возможность повторно активировать лицензию на другом устройстве
    public Ticket activateLicense(UUID activationCode, Device device, User user) throws Exception {
        License license = requireByCode(activationCode);

        //TODO 5.
        validateActivation(license, device, user);

        if (license.getFirstActivationDate() == null) {
            updateLicenseForActivation(license, user);
        }

        deviceLicenseService.createDeviceLicense(license, device);

        licenseHistoryService.userActivate(license, user);

        return generateTicket(license, device);
    }

    //TODO: 2. getActiveLicenseForDevice - судя по содержанию, лицензия достаётся не для конкретного устройства. Либо переименовать, либо изменить сам метод
    public List<License> getActiveLicensesForDevice(Device device) throws LicenseException {
        List<DeviceLicense> deviceLicenses = deviceLicenseService.getByDeviceId(device.getId());
        List<License> activeLicenses = new ArrayList<>();

        for (DeviceLicense deviceLicense : deviceLicenses) {
            Long licenseId = deviceLicense.getLicense().getId();

            License license = licenseRepository.findById(licenseId).orElse(null);

            if (license.getIsBlocked()) {
                continue; // Пропускаем заблокированные лицензии
            }

            activeLicenses.add(license);
        }

        return activeLicenses;
    }
    
    public Ticket updateExistentLicense(UUID licenseCode, User user, Device device) throws Exception {
        License license = requireByCode(licenseCode);

        if (license.getIsBlocked()) {
            throw new LicenseException("License is blocked");
        }

        if (license.getFirstActivationDate() == null) {
            throw new LicenseException("License is not activated");
        }

        license.setEndingDate(
                new Date(license.getEndingDate().getTime() + license.getDuration())
        );

        licenseRepository.save(license);

        licenseHistoryService.userUpdate(license, user);

        return generateTicket(
                license,
                device
        );
    }

    //TODO: 3. generateLicenseResponse - получается, что лицензия в тикете всегда разблокирована
    public Ticket generateTicket(License license, Device device) throws Exception {
        Ticket ticket = new Ticket();

        ticket.setCurrentDate(new Date());
        ticket.setLifetime(license.getDuration());
        ticket.setActivationDate(license.getFirstActivationDate());
        ticket.setExpirationDate(license.getEndingDate());
        ticket.setUserId(device.getUser().getId());
        ticket.setDeviceId(device.getId());
        ticket.setIsBlocked(license.getIsBlocked());

        String digitalSignature = cryptoService.signWithCurrent(ticket.toString());
        ticket.setDigitalSignature(digitalSignature);

        return ticket;
    }

    private void validateActivation(License license, Device device, User user) throws LicenseException {
        if (license.getUser() != null) {
            if (license.getUser().getId() != user.getId()) {
                throw new LicenseException("License already activated by another user");
            }
        }

        if (license.getIsBlocked()) {
            throw new LicenseException("License is blocked");
        }

        if (license.getEndingDate() != null) {
            if (license.getEndingDate().before(new Date())) {
                throw new LicenseException("License is expired");
            }
        }

        if (license.getDevicesCount() <= deviceLicenseService.getByLicenseId(license.getId()).size()) {
            throw new LicenseException("Device count exceeded");
        }
    }

    private void updateLicenseForActivation(License license, User user) {
        license.setFirstActivationDate(new Date());
        license.setEndingDate(
                new Date(System.currentTimeMillis() + license.getDuration())
        );
        license.setUser(user);

        licenseRepository.save(license);
    }

    public void delete(Long id) {
        License license = requireById(id);

        licenseRepository.delete(license);
    }
}
