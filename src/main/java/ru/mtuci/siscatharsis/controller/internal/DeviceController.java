package ru.mtuci.siscatharsis.controller.internal;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.siscatharsis.dto.internal.DeviceRequest;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.services.DeviceService;
import ru.mtuci.siscatharsis.repositories.DeviceRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDController;

import java.util.List;
import java.util.Objects;

//TODO: 1. Пересмотреть права доступа для простого пользователя во всех контроллерах

@RestController
@RequestMapping("/admin/device")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class DeviceController extends AbstractCRUDController<Device, DeviceRequest, DeviceRepository, DeviceService>{

    @Autowired
    public DeviceController(DeviceService deviceService) {
        super(deviceService);
    }
}
