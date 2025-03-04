package ru.mtuci.siscatharsis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.mtuci.siscatharsis.dto.internal.request.LicenseTypeRequest;
import ru.mtuci.siscatharsis.model.LicenseType;
import ru.mtuci.siscatharsis.repositories.LicenseTypeRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDService;

@Service
public class LicenseTypeService extends AbstractCRUDService<LicenseType, LicenseTypeRepository>{

    @Autowired
    public LicenseTypeService(LicenseTypeRepository repository) {
        super(repository, LicenseType.class);
    }

    public LicenseType create(LicenseTypeRequest licenseTypeRequest) {
        return repository.save(
            new LicenseType(
                licenseTypeRequest.getName(),
                licenseTypeRequest.getDefaultDuration(),
                licenseTypeRequest.getDescription(),
                licenseTypeRequest.getDefaultDeviceCount()
            )
        );
    }

    public LicenseType update(Long id, LicenseTypeRequest licenseTypeRequest) {
        LicenseType licenseType = this.findById(id);

        licenseType.setName(licenseTypeRequest.getName());
        licenseType.setDuration(licenseTypeRequest.getDefaultDuration());
        licenseType.setDescription(licenseTypeRequest.getDescription());
        licenseType.setDeviceCount(licenseTypeRequest.getDefaultDeviceCount());

        return repository.save(licenseType);
    }
}
