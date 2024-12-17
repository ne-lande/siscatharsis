package ru.mtuci.siscatharsis.controller.internal;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.model.LicenseHistory;
import ru.mtuci.siscatharsis.dto.internal.LicenseHistoryRequest;
import ru.mtuci.siscatharsis.services.LicenseHistoryService;
import ru.mtuci.siscatharsis.repositories.LicenseHistoryRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDController;

import java.util.List;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/admin/license-history")
public class LicenseHistoryController extends AbstractCRUDController<LicenseHistory, LicenseHistoryRequest, LicenseHistoryRepository, LicenseHistoryService> {

    @Autowired
    public LicenseHistoryController(LicenseHistoryService service) {
        super(service);
    }
}
