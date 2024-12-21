package ru.mtuci.siscatharsis.services;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.base.AbstractCRUDService;
import ru.mtuci.siscatharsis.dto.internal.LicenseRequest;
import ru.mtuci.siscatharsis.dto.license.LicenseResponse;
import ru.mtuci.siscatharsis.model.*;
import ru.mtuci.siscatharsis.repositories.LicenseRepository;
import ru.mtuci.siscatharsis.services.*;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;
import ru.mtuci.siscatharsis.utils.LicenseException;

@Service
public class LicenseService
    extends AbstractCRUDService<License, LicenseRequest, LicenseRepository> {

    private final ProductService productService;
    private final UserService userService;
    private final LicenseTypeService licenseTypeService;
    private final LicenseHistoryService licenseHistoryService;
    private final DeviceService deviceService;
    private final DeviceLicenseService deviceLicenseService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LicenseService(
        LicenseRepository repository,
        ProductService productService,
        UserService userService,
        LicenseTypeService licenseTypeService,
        LicenseHistoryService licenseHistoryService,
        DeviceLicenseService deviceLicenseService,
        DeviceService deviceService,
        PasswordEncoder passwordEncoder
    ) {
        super(repository, License.class);
        this.productService = productService;
        this.userService = userService;
        this.deviceService = deviceService;
        this.licenseTypeService = licenseTypeService;
        this.licenseHistoryService = licenseHistoryService;
        this.deviceLicenseService = deviceLicenseService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public License create(
        LicenseRequest licenseRequest
    ) /*throws IllegalArgumentException*/{
        Product product = productService.findById(
            licenseRequest.getProductId()
        );
        User user = userService.findById(licenseRequest.getUserId());
        LicenseType licenseType = licenseTypeService.findById(
            licenseRequest.getTypeId()
        );

        String code = generateLicenseCode(licenseRequest);

        License license = new License();
        license.setCode(code);
        license.setUser(null);
        license.setProduct(product);
        license.setType(licenseType);
        license.setFirstActivationDate(null);
        license.setEndingDate(null);
        license.setIsBlocked(false);
        license.setDevicesCount(licenseRequest.getDeviceCount());
        license.setOwner(user);
        license.setDuration(licenseRequest.getDuration());
        license.setDescription(licenseRequest.getDescription());
        license.setProduct(product);

        repository.save(license);

        licenseHistoryService.save(
            new LicenseHistory(
                license,
                user,
                "CREATED",
                new Date(),
                "License created"
            )
        );

        return license;
    }

    @Override
    public License update(Long id, LicenseRequest licenseRequest) {
        License license = this.findById(id);
        User user = userService.findById(licenseRequest.getUserId());
        Product product = productService.findById(
            licenseRequest.getProductId()
        );
        LicenseType licenseType = licenseTypeService.findById(
            licenseRequest.getTypeId()
        );

        license.setCode(licenseRequest.getCode());
        license.setUser(null);
        license.setProduct(product);
        license.setType(licenseType);
        license.setFirstActivationDate(null);
        license.setEndingDate(null);
        license.setIsBlocked(false);
        license.setDevicesCount(licenseRequest.getDeviceCount());
        license.setOwner(user);
        license.setDuration(licenseRequest.getDuration());
        license.setDescription(licenseRequest.getDescription());
        license.setProduct(product);

        licenseHistoryService.save(
            new LicenseHistory(
                license,
                user,
                "UPDATE",
                new Date(),
                "License updated"
            )
        );

        return repository.save(license);
    }

    public License findByCode(String code) {
        return repository
            .findByCode(code)
            .orElseThrow(() ->
                new EntityNotFoundException(
                    "License not found with the given activation code"
                )
            );
    }

    public LicenseResponse activateLicense(
        String activationCode,
        Device device,
        String login
    ) throws IllegalArgumentException, LicenseException {
        License license = this.findByCode(activationCode);
        User user = userService.findByLogin(login);

        if (license.getUser() != null) {
            if (license.getUser().getId().equals(user.getId())) {
                throw new LicenseException("License already activated");
            }
        }

        validateActivation(license, device, login);

        if (license.getFirstActivationDate() == null) {
            updateLicenseForActivation(license, user);
        }

        deviceLicenseService.createDeviceLicense(license, device);

        licenseHistoryService.save(
            new LicenseHistory(
                license,
                license.getOwner(),
                "ACTIVATED",
                new Date(),
                "License activated"
            )
        );

        return generateLicenseResponse(license, device);
    }

    public License getActiveLicenseForDevice(
        Device device,
        User user,
        String code
    ) throws LicenseException {
        License license = this.findByCode(code);
        DeviceLicense deviceLicense =
            deviceLicenseService.findByDeviceIdAndLicenseId(
                device.getId(),
                license.getId()
            );

        if (license.getIsBlocked()) {
            throw new LicenseException("License is blocked");
        }

        return license;
    }

    public LicenseResponse updateExistentLicense(
        String licenseCode,
        String login,
        String macAddress
    ) throws IllegalArgumentException, LicenseException {
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

        return generateLicenseResponse(
            license,
            deviceService.findByMacAddress(macAddress)
        );
    }

    public LicenseResponse generateLicenseResponse(
        License license,
        Device device
    ) {
        LicenseResponse licenseResponse = new LicenseResponse();

        licenseResponse.setCurrentDate(new Date());
        licenseResponse.setLifetime(license.getDuration());
        licenseResponse.setActivationDate(
            new Date(license.getFirstActivationDate().getTime())
        );
        licenseResponse.setExpirationDate(
            new Date(license.getEndingDate().getTime())
        );
        licenseResponse.setUserId(license.getOwner().getId());
        licenseResponse.setDeviceId(device.getId());
        licenseResponse.setIsBlocked(false);
        licenseResponse.setSignature(generateSignature(licenseResponse));

        return licenseResponse;
    }

    private void validateActivation(
        License license,
        Device device,
        String login
    ) throws LicenseException {
        if (license.getIsBlocked()) {
            throw new LicenseException("License is blocked");
        }

        if (
            license.getEndingDate() != null ||
            license.getEndingDate().before(new Date())
        ) {
            throw new LicenseException("License is expired");
        }

        if (
            license.getDevicesCount() <=
            deviceLicenseService.findByLicense(license).size()
        ) {
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

    public String generateSignature(LicenseResponse licenseResponse) {
        return passwordEncoder.encode(licenseResponse.getBodyForSigning());
    }

    private String generateLicenseCode(LicenseRequest licenseRequest) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data =
                licenseRequest.getProductId() +
                licenseRequest.getUserId() +
                licenseRequest.getTypeId() +
                licenseRequest.getDeviceCount() +
                licenseRequest.getDuration() +
                licenseRequest.getDescription() +
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(
                    LocalDateTime.now()
                );
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating license code", e);
        }
    }
}
