package ru.mtuci.siscatharsis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.mtuci.siscatharsis.base.AbstractCRUDService;
import ru.mtuci.siscatharsis.dto.external.license.response.Ticket;
import ru.mtuci.siscatharsis.dto.internal.license.request.LicenseUpdateRequest;
import ru.mtuci.siscatharsis.dto.internal.license.request.LicenseCreateRequest;
import ru.mtuci.siscatharsis.model.*;
import ru.mtuci.siscatharsis.repositories.LicenseRepository;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;
import ru.mtuci.siscatharsis.utils.LicenseException;
import ru.mtuci.siscatharsis.utils.CryptoUtil;

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
public class LicenseService extends AbstractCRUDService<License, LicenseRepository> {
    private final ProductService productService;
    private final UserService userService;
    private final LicenseTypeService licenseTypeService;
    private final LicenseHistoryService licenseHistoryService;
    private final DeviceService deviceService;
    private final DeviceLicenseService deviceLicenseService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LicenseService(LicenseRepository repository, ProductService productService, UserService userService,
                          LicenseTypeService licenseTypeService, LicenseHistoryService licenseHistoryService,
                          DeviceLicenseService deviceLicenseService, DeviceService deviceService,
                          PasswordEncoder passwordEncoder) {
        super(repository, License.class);
        this.productService = productService;
        this.userService = userService;
        this.deviceService = deviceService;
        this.licenseTypeService = licenseTypeService;
        this.licenseHistoryService = licenseHistoryService;
        this.deviceLicenseService = deviceLicenseService;
        this.passwordEncoder = passwordEncoder;
    }

    //TODO: 6. Дублируется код в create и update
    //TODO: 7. create - license.setDuration(licenseRequest.getDuration()); Лучше сделать в LicenceType свойство - defaultDuration и его использовать
    //TODO: 8. create - license.setProduct(product); Присутствует дважды
    public License create(LicenseCreateRequest licenseRequest) {
        User owner = userService.findById(licenseRequest.getOwnerId());
        Product product = productService.findById(licenseRequest.getProductId());
        LicenseType licenseType = licenseTypeService.findById(licenseRequest.getTypeId());

        License license = new License(
            owner,
            product,
            licenseType
        );

        license.setEndingDate(licenseRequest.getEndingDate());
        license.setDescription(licenseRequest.getDescription());

        repository.save(license);

        licenseHistoryService.save(LicenseHistory.create(license));

        return license;
    }

    //TODO: 6. Дублируется код в create и update
    //TODO: 9. update - если меняются свойства существующей лицензии, то не нужно менять код активации и владельца
    public License update(Long id, LicenseUpdateRequest licenseRequest) {
        User user = userService.findById(licenseRequest.getUserId());
        Product product = productService.findById(licenseRequest.getProductId());
        LicenseType licenseType = licenseTypeService.findById(licenseRequest.getTypeId());

        License license = this.findById(id);

        license.setUser(user);
        license.setProduct(product);
        license.setType(licenseType);

        license.setFirstActivationDate(licenseRequest.getFirstActivationDate());
        license.setEndingDate(licenseRequest.getEndingDate());

        license.setIsBlocked(licenseRequest.getIsBlocked());
        license.setDevicesCount(licenseRequest.getDeviceCount());
        license.setDuration(licenseRequest.getDuration());
        license.setDescription(licenseRequest.getDescription());

        licenseHistoryService.save(LicenseHistory.update(license));

        return repository.save(license);
    }

    public License findByCode(UUID code) {
        return repository
                .findByCode(code)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "License not found with the given activation code"
                        )
                );
    }

    //TODO: 5. activateLicense - пересмотреть проверку. Пользователь должен иметь возможность повторно активировать лицензию на другом устройстве
    public Ticket activateLicense(UUID activationCode, Device device, User user) throws Exception {
        License license = this.findByCode(activationCode);

        //TODO 5.
        validateActivation(license, device, user);

        if (license.getFirstActivationDate() == null) {
            updateLicenseForActivation(license, user);
        }

        deviceLicenseService.createDeviceLicense(license, device);

        licenseHistoryService.save(LicenseHistory.activate(license));

        return generateTicket(license, device);
    }

    //TODO: 2. getActiveLicenseForDevice - судя по содержанию, лицензия достаётся не для конкретного устройства. Либо переименовать, либо изменить сам метод
    public List<License> getActiveLicensesForDevice(Device device) throws LicenseException {
        List<DeviceLicense> deviceLicenses = deviceLicenseService.getByDeviceId(device.getId());
        List<License> activeLicenses = new ArrayList<>();

        for (DeviceLicense deviceLicense : deviceLicenses) {
            License license = this.findById(deviceLicense.getLicense().getId());

            if (license.getIsBlocked()) {
                continue; // Пропускаем заблокированные лицензии
            }

            activeLicenses.add(license);
        }

        return activeLicenses;
    }
    
    public Ticket updateExistentLicense(UUID licenseCode,String login, String macAddress) throws Exception {
        License license = this.findByCode(licenseCode);

        if (license.getIsBlocked()) {
            throw new LicenseException("License is blocked");
        }

        if (license.getFirstActivationDate() == null) {
            throw new LicenseException("License is not activated");
        }

        license.setEndingDate(
                new Date(license.getEndingDate().getTime() + license.getDuration())
        );
        repository.save(license);

        licenseHistoryService.save(
                new LicenseHistory(
                        license,
                        license.getOwner(),
                        "UPDATED BY USER",
                        new Date(),
                        "License updated"
                )
        );

        User user = userService.findByLogin(login);

        return generateTicket(
                license,
                deviceService.findByMacAddressAndUser(macAddress, user)
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

        ticket.setSignature(CryptoUtil.sign(ticket.toString()));

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

        repository.save(license);
    }
}
