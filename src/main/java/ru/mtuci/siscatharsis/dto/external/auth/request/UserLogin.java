package ru.mtuci.siscatharsis.dto.external.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLogin {

    @NotBlank(message = "Login is empty")
    private String login;

    @NotBlank(message = "Password is empty")
    private String password;

    private Long deviceId;
}
