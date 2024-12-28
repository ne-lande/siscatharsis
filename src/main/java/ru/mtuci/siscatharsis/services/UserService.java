package ru.mtuci.siscatharsis.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.dto.internal.UserRequest;
import ru.mtuci.siscatharsis.repositories.UserRepository;
import ru.mtuci.siscatharsis.base.AbstractCRUDService;


import java.util.List;
import java.util.Optional;

@Service
public class UserService extends AbstractCRUDService<User, UserRepository> implements UserDetailsService {

    @Autowired
    public UserService(UserRepository repository) {
        super(repository, User.class);
    }

    public User create(UserRequest userRequest) {
        return null;
    }

    public User update(Long id, UserRequest userRequest) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return this.findByLogin(login);
    }

    public User findByLogin(String login){
        return repository.findByLogin(login)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found by login")
        );
    }

    public User findByEmail(String email){
        return repository.findByEmail(email)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found by email")
            );
    }
}
