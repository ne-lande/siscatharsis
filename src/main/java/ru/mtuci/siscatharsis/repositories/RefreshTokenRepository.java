package ru.mtuci.siscatharsis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.siscatharsis.model.RefreshToken;
import ru.mtuci.siscatharsis.model.User;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
        Optional<RefreshToken> findByToken(String token);
        Optional<RefreshToken> findByUser(User user);
}
