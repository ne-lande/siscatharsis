package ru.mtuci.siscatharsis.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.repositories.DeviceRepository;
import ru.mtuci.siscatharsis.utils.EntityAlreadyExistException;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;

    // Предполагается что девайс ТОЧНО будет найден, любое другое поведение -> хуйня
    public Device requireUserDevice(String macAddress, User user) {
        return deviceRepository.findByMacAddressAndUser(macAddress, user).orElseThrow(
                () -> new EntityNotFoundException("Device not found")
        );
    }

    public Boolean existsUserDevice(String macAddress, User user) {
        return deviceRepository.findByMacAddressAndUser(macAddress, user).isPresent();
    }

    public Page<Device> getAllDevices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return deviceRepository.findAll(pageable);
    }

    public Device findById(Long deviceId) {
        return deviceRepository.findById(deviceId).orElse(null);
    }

    public Device requireById(Long deviceId) {
        return deviceRepository.findById(deviceId).orElseThrow(
                () -> new EntityNotFoundException("Device not found by id")
        );
    }

    public List<Device> getByUserId(Long userId) {
        return deviceRepository.getByUserId(userId);
    }

    // девайс к одному юзеру
    public Device registerOrUpdateDevice(String macAddress, User user) {
        Device device = deviceRepository.findByMacAddressAndUser(macAddress, user).orElse(
                Device.builder()
                        .user(user)
                        .macAddress(macAddress)
                        .build()
        );

        return deviceRepository.save(device);
    }

    // CRUD

    public Device create(Device device) {
        String macAddress = device.getMacAddress();

        User user = device.getUser();
        if (existsUserDevice(macAddress, user)) {
            throw new EntityAlreadyExistException("Device already exists");
        }

        return deviceRepository.save(device);
    }

    public Device update(Long id, Device newDevice) {
        String macAddress = newDevice.getMacAddress();
        User user = newDevice.getUser();

        if (existsUserDevice(macAddress, user)) {
            throw new EntityAlreadyExistException("Device with same mac already exists");
        }

        Device device = requireById(id);

        device.setUser(user);
        device.setName(newDevice.getName());
        device.setMacAddress(macAddress);

        return deviceRepository.save(device);
    }

    public void delete(Long id) {
        Device device = requireById(id);

        deviceRepository.delete(device);
    }
}
