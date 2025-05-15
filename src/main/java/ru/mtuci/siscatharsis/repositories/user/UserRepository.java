package ru.mtuci.siscatharsis.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.siscatharsis.model.user.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);
    Optional<User> findByEmail(String email);
    Boolean existsByLoginAndEmail(String login, String email);
}
