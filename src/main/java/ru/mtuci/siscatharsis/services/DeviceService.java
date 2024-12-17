package ru.mtuci.siscatharsis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.dto.license.LicenseActivationRequest;
import ru.mtuci.siscatharsis.dto.internal.DeviceRequest;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.repositories.DeviceRepository;
import ru.mtuci.siscatharsis.repositories.UserRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDService;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceService extends AbstractCRUDService<Device, DeviceRequest, DeviceRepository> {

    private final UserService userService;

    @Autowired
    public DeviceService(DeviceRepository repository, UserService userService) {
        super(repository, Device.class);
        this.userService = userService;
    }

    @Override
    public Device create(DeviceRequest deviceRequest) {
        User user = userService.findById(deviceRequest.getUserId());

        Device device = new Device();
        device.setName(deviceRequest.getDeviceName());
        device.setMacAddress(deviceRequest.getMacAddress());
        device.setUser(user);
        return repository.save(device);
    }

    @Override
    public Device update(Long id, DeviceRequest deviceRequest) {
        Device device = this.findById(id);
        User user = userService.findById(deviceRequest.getUserId());

        device.setName(deviceRequest.getDeviceName());
        device.setMacAddress(deviceRequest.getMacAddress());
        device.setUser(user);
        return repository.save(device);
    }

    public Device findByMacAddressAndUser(String macAddress, User user) {
        return repository.findByMacAddressAndUser(macAddress, user)
            .orElseThrow(() -> new EntityNotFoundException(
                "Device not found by macaddress and user"
        ));
    }

    public Device findByMacAddress(String macAddress) {
        return repository.findByMacAddress(macAddress)
            .orElseThrow(() -> new EntityNotFoundException(
                "Device not found by macaddress"
        ));
    }

    public Device registerOrUpdateDevice(LicenseActivationRequest activationRequest, User user) {
        Device device;
        try {
            device = this.findByMacAddress(activationRequest.getMacAddress());
            if (!device.getUser().equals(user)) {
                throw new IllegalArgumentException("Device already registered by another user");
            }
        } catch (EntityNotFoundException e) {
            device = new Device();
            device.setMacAddress(activationRequest.getMacAddress());
            device.setUser(user);
        }

        device.setName(activationRequest.getDeviceName());

        return repository.save(device);
    }
}
