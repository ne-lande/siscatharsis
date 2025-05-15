package ru.mtuci.siscatharsis.controller.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import ru.mtuci.siscatharsis.dto.user.UserCreateUpdateRequest;
import ru.mtuci.siscatharsis.model.user.User;
import ru.mtuci.siscatharsis.services.user.UserService;
import ru.mtuci.siscatharsis.utils.ResponseUtils;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/admin/user")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ResponseUtils responseUtils;

    @GetMapping("/")
    public ResponseEntity<?> readAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<User> users = userService.getAllUsers(page, size);

        return responseUtils.success(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readUser(@PathVariable Long id) {
        User user = userService.requireById(id);

        return responseUtils.success(user);
    }

    @PostMapping("/")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateUpdateRequest userRequest) {
        User user = userFromDto(userRequest);

        userService.create(user, userRequest.password());

        return responseUtils.success(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserCreateUpdateRequest userRequest) {
        User user = userFromDto(userRequest);

        userService.update(id, user, userRequest.password());

        return responseUtils.success(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.delete(id);

        return responseUtils.success(id);
    }

    private User userFromDto(UserCreateUpdateRequest dto) {
        return User.builder()
                .login(dto.login())
                .email(dto.email())
                .role(dto.role())
                .build();
    }
}
