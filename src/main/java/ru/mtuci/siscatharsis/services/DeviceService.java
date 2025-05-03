package ru.mtuci.siscatharsis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.repositories.DeviceRepository;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;

import java.util.List;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, UserService userService) {
        this.deviceRepository = deviceRepository;
    }

    // Предполагается что девайс ТОЧНО будет найден, любое другое поведение -> хуйня
    public Device requireUserDevice(String macAddress, User user) {
        return deviceRepository.findByMacAddressAndUser(macAddress, user)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Device not found"
                ));
    }

    public Boolean existsUserDevice(String macAddress, User user) {
        return deviceRepository.findByMacAddressAndUser(macAddress, user).isPresent();
    }

    public Device findById(Long deviceId) {
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Device not found by id"
                ));
    }

    public List<Device> getByUserId(Long userId) {
        return deviceRepository.getByUserId(userId);
    }

    // девайс к одному юзеру
    public Device registerOrUpdateDevice(String macAddress, User user) {
        Device device = deviceRepository.findByMacAddressAndUser(macAddress, user)
                .orElse(
                        Device.builder()
                                .user(user)
                                .macAddress(macAddress)
                                .build()
                );

        return deviceRepository.save(device);
    }

    // CRUD

    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    public void delete(Device device) {
        deviceRepository.delete(device);
    }
}
