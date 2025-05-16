package ru.mtuci.siscatharsis.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.mtuci.siscatharsis.model.user.User;

public record UserCreateUpdateRequest(
    @NotBlank(message = "Login is empty")
    String login,

    @NotBlank(message = "Password is empty")
    String password,

    @NotBlank(message = "Email is empty")
    String email,

    @NotNull(message = "Role cannot be null")
    User.Role role
) {}
