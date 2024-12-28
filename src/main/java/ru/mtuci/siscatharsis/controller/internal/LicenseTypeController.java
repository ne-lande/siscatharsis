package ru.mtuci.siscatharsis.controller.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.internal.LicenseTypeRequest;
import ru.mtuci.siscatharsis.model.LicenseType;
import ru.mtuci.siscatharsis.services.LicenseTypeService;
import ru.mtuci.siscatharsis.repositories.LicenseTypeRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDController;
import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
@RequestMapping("/admin/license-type")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class LicenseTypeController extends AbstractCRUDController<LicenseType, LicenseTypeRepository, LicenseTypeService>{

    @Autowired
    public LicenseTypeController(LicenseTypeService service) {
        super(service);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody LicenseTypeRequest requestDTO) {
        return ApiMessage.Success(service.create(requestDTO));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody LicenseTypeRequest requestDTO) {
        return ApiMessage.Success(service.update(id, requestDTO));
    }
}
