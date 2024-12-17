package ru.mtuci.siscatharsis.controller.internal;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.internal.LicenseTypeRequest;
import ru.mtuci.siscatharsis.model.LicenseType;
import ru.mtuci.siscatharsis.services.LicenseTypeService;
import ru.mtuci.siscatharsis.repositories.LicenseTypeRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/admin/license-type")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class LicenseTypeController extends AbstractCRUDController<LicenseType, LicenseTypeRequest, LicenseTypeRepository, LicenseTypeService>{

    @Autowired
    public LicenseTypeController(LicenseTypeService service) {
        super(service);
    }
}
