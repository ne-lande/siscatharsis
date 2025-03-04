package ru.mtuci.siscatharsis.controller.internal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.base.AbstractCRUDController;
import ru.mtuci.siscatharsis.dto.internal.request.UserRequest;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.repositories.UserRepository;

@RestController
@RequestMapping("/admin/user")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserController extends AbstractCRUDController<User, UserRepository, UserService> {

    //private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService service, PasswordEncoder passwordEncoder) {
        super(service);
        this.passwordEncoder = passwordEncoder;
    }

    protected User createEntity(UserRequest userRequest) {
        if (service.findByLogin(userRequest.getLogin()) != null) {
            throw new IllegalArgumentException("Login already assigned");
        }

        if (service.findByEmail(userRequest.getEmail()) != null) {
            throw new IllegalArgumentException("Email already assigned");
        }

        User user = new User(userRequest.getLogin(), passwordEncoder.encode(userRequest.getPassword()), userRequest.getEmail(), userRequest.getRole(), null);
        service.save(user);
        return user;
    }

    protected User updateEntity(Long id, UserRequest userRequest) {
        User user = service.findById(id);

        if (service.findByLogin(userRequest.getLogin()) != null) {
            throw new IllegalArgumentException("Login already assigned");
        }

        if (service.findByEmail(userRequest.getEmail()) != null) {
            throw new IllegalArgumentException("Email already assigned");
        }

        user.setLogin(userRequest.getLogin());
        user.setEmail(userRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(userRequest.getRole());

        service.save(user);
        return user;
    }
}
