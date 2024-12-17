package ru.mtuci.siscatharsis.dto.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mtuci.siscatharsis.enums.UserRoleEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @NotBlank(message = "Login is empty")
    private String login;

    @NotBlank(message = "Password is empty")
    private String password;

    @NotBlank(message = "Email is empty")
    private String email;

    @NotEmpty(message = "Role is empty")
    private UserRoleEnum role;
}
