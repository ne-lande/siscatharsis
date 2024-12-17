package ru.mtuci.siscatharsis.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.User;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByMacAddressAndUser(String macAddress, User user);
    Optional<Device> findByMacAddress(String macAddress);
    Optional<Device> findByUser(User user);
}
