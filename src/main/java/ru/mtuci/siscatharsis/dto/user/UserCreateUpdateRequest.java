package ru.mtuci.siscatharsis.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import ru.mtuci.siscatharsis.model.User;

public record UserCreateUpdateRequest(
    @NotBlank(message = "Login is empty")
    String login,

    @NotBlank(message = "Password is empty")
    String password,

    @NotBlank(message = "Email is empty")
    String email,

    @NotEmpty(message = "Role is empty")
    User.Role role
) {}
