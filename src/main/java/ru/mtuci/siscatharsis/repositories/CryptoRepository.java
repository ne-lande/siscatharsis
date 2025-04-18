package ru.mtuci.siscatharsis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.siscatharsis.model.Crypto;

public interface CryptoRepository extends JpaRepository<Crypto, Long> {
}
