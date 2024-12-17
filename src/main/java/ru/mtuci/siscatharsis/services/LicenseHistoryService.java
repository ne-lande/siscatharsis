package ru.mtuci.siscatharsis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.mtuci.siscatharsis.model.LicenseHistory;
import ru.mtuci.siscatharsis.dto.internal.LicenseHistoryRequest;
import ru.mtuci.siscatharsis.repositories.LicenseHistoryRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDService;

import java.util.List;

@Service
public class LicenseHistoryService extends AbstractCRUDService<LicenseHistory, LicenseHistoryRequest, LicenseHistoryRepository> {

    /* BIG EXPLANATION

    - As it is serves as sub-service we cant provide plain create and delete, only query and deletion.
    There's two reasons for this:
        1) Due to circular import (skill issue)
        2) Due to the service itself goals
    */

    @Autowired
    public LicenseHistoryService(LicenseHistoryRepository repository) {
        super(repository, LicenseHistory.class);
    }

    @Override
    public LicenseHistory create(LicenseHistoryRequest licenseHistoryRequest) {
        return null;
    }

    @Override
    public LicenseHistory update(Long id, LicenseHistoryRequest licenseHistoryRequest) {
        return null;
    }
}
