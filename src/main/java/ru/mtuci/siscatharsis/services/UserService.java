package ru.mtuci.siscatharsis.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

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
                () -> new UsernameNotFoundException("User not found by id")
        );
    }

    public Boolean existsByLoginAndEmail(String login, String email) {
        return userRepository.existsByLoginAndEmail(login, email);
    }

    public User create(User user) {
        String login = user.getLogin();
        String email = user.getEmail();

        if (existsByLoginAndEmail(login, email)) {
            throw new IllegalArgumentException("Login already assigned");
        }

        userRepository.save(user);

        return user;
    }

    public User update(Long id, User newUser) {
        User user = requireById(id);

        user.setLogin(newUser.getLogin());
        user.setEmail(newUser.getEmail());
        user.setPasswordHash(newUser.getPasswordHash());
        user.setRole(newUser.getRole());

        userRepository.save(user);

        return user;
    }

    public void delete(Long id) {
        User user = requireById(id);

        userRepository.delete(user);
    }
}
