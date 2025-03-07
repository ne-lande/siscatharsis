package ru.mtuci.siscatharsis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.siscatharsis.model.Device;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.model.UserSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
        List<UserSession> getByUser(User user);
        Optional<UserSession> findByUserAndDevice(User user, Device device);
        Optional<UserSession> findByRefreshTokenId(UUID refreshTokenId);
}