package ru.mtuci.siscatharsis.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ru.mtuci.siscatharsis.dto.internal.request.LicenseTypeRequest;
import ru.mtuci.siscatharsis.model.LicenseType;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.repositories.LicenseTypeRepository;

@Service
@RequiredArgsConstructor
public class LicenseTypeService {

    private final LicenseTypeRepository licenseTypeRepository;

    public Page<LicenseType> getAllLicenseTypes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return licenseTypeRepository.findAll(pageable);
    }

    public LicenseType findById(Long id) {
        return licenseTypeRepository.findById(id).orElse(null);
    }

    public LicenseType requireById(Long id) {
        return licenseTypeRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("a")
        );
    }

    public LicenseType create(LicenseType licenseType) {
        return licenseTypeRepository.save(licenseType);
    }

    public LicenseType update(Long id, LicenseType newLicenseType) {
        LicenseType licenseType = findById(id);

        licenseType.setName(newLicenseType.getName());
        licenseType.setDuration(newLicenseType.getDuration());
        licenseType.setDescription(newLicenseType.getDescription());
        licenseType.setDeviceCount(newLicenseType.getDeviceCount());

        return licenseTypeRepository.save(licenseType);
    }

    public void delete(Long id) {
        LicenseType licenseType = requireById(id);

        licenseTypeRepository.delete(licenseType);
    }
}
