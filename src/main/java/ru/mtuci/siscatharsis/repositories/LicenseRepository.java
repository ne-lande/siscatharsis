package ru.mtuci.siscatharsis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.siscatharsis.model.License;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
    Optional<License> findByCode(UUID code);
}
