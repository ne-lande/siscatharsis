package ru.mtuci.siscatharsis.controller.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.internal.DeviceRequest;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.repositories.DeviceRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDController;
import ru.mtuci.siscatharsis.utils.ApiMessage;

//TODO: 1. Пересмотреть права доступа для простого пользователя во всех контроллерах

@RestController
@RequestMapping("/admin/device")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class DeviceController extends AbstractCRUDController<Device, DeviceRepository, DeviceService>{

    @Autowired
    public DeviceController(DeviceService deviceService) {
        super(deviceService);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody DeviceRequest requestDTO) {
        return ApiMessage.Success(service.create(requestDTO));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody DeviceRequest requestDTO) {
        return ApiMessage.Success(service.update(id, requestDTO));
    }
}
