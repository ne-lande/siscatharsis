package ru.mtuci.siscatharsis.controller.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.model.LicenseHistory;
import ru.mtuci.siscatharsis.services.LicenseHistoryService;
import ru.mtuci.siscatharsis.repositories.LicenseHistoryRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDController;

// выглядит как служебный класс поэтому врядли ему нужно что-то настолько абстрактное
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/admin/license-history")
public class LicenseHistoryController extends AbstractCRUDController<LicenseHistory, LicenseHistoryRepository, LicenseHistoryService> {

    @Autowired
    public LicenseHistoryController(LicenseHistoryService service) {
        super(service);
    }
}
