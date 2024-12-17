package ru.mtuci.siscatharsis.controller.internal;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.model.*;
import ru.mtuci.siscatharsis.dto.internal.LicenseRequest;
import ru.mtuci.siscatharsis.services.LicenseService;
import ru.mtuci.siscatharsis.repositories.LicenseRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDController;
import java.util.Objects;
import java.util.List;

@RestController
@RequestMapping("/admin/license")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class LicenseController extends AbstractCRUDController<License, LicenseRequest, LicenseRepository, LicenseService> {
    @Autowired
    public LicenseController(LicenseService service) {
        super(service);
    }
}
