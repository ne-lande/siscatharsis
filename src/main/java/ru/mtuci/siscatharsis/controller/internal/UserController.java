package ru.mtuci.siscatharsis.controller.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.user.UserCreateUpdateRequest;
import ru.mtuci.siscatharsis.model.User;
import ru.mtuci.siscatharsis.services.UserService;
import ru.mtuci.siscatharsis.utils.ApiMessage;

@RestController
@RequestMapping("/admin/user")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public ResponseEntity<?> readAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<User> users = userService.getAllUsers(page, size);

        return ApiMessage.Success(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readUser(@PathVariable Long id) {
        User user = userService.requireById(id);

        return ApiMessage.Success(user);
    }

    @PostMapping("/")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateUpdateRequest userRequest) {
        String passwordHash = passwordEncoder.encode(userRequest.password());

        User user = User.builder()
                .login(userRequest.login())
                .email(userRequest.email())
                .role(userRequest.role())
                .passwordHash(passwordHash)
                .build();

        userService.create(user);

        return ApiMessage.Success(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserCreateUpdateRequest userRequest) {
        String passwordHash = passwordEncoder.encode(userRequest.password());

        User user = User.builder()
                .login(userRequest.login())
                .email(userRequest.email())
                .role(userRequest.role())
                .passwordHash(passwordHash)
                .build();

        userService.update(id, user);

        return ApiMessage.Success(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.delete(id);

        return ApiMessage.Success(id);
    }
}
