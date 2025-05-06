package ru.mtuci.siscatharsis.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.repositories.UserRepository;
import ru.mtuci.siscatharsis.utils.EntityAlreadyExistException;
import ru.mtuci.siscatharsis.utils.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return this.findByLogin(login);
    }

    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    public User findByLogin(String login){
        return userRepository.findByLogin(login).orElse(null);
    }

    public User requireByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(
                () -> new UsernameNotFoundException("User not found by login")
        );
    }

    public User requireById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found by id")
        );
    }

    public Boolean existsByLoginAndEmail(String login, String email) {
        return userRepository.existsByLoginAndEmail(login, email);
    }

    public User create(User user, String password) {
        String login = user.getLogin();
        String email = user.getEmail();

        if (existsByLoginAndEmail(login, email)) {
            throw new EntityAlreadyExistException("Login already assigned");
        }

        String passwordHash = passwordEncoder.encode(password);
        user.setPasswordHash(passwordHash);

        userRepository.save(user);

        return user;
    }

    public User update(Long id, User newUser, String password) {
        User user = requireById(id);

        String login = newUser.getLogin();
        String email = newUser.getEmail();

        if (existsByLoginAndEmail(login, email)) {
            throw new EntityAlreadyExistException("Login already assigned");
        }

        String passwordHash = passwordEncoder.encode(password);

        user.setLogin(login);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setRole(newUser.getRole());

        userRepository.save(user);

        return user;
    }

    public void delete(Long id) {
        User user = requireById(id);

        userRepository.delete(user);
    }
}
