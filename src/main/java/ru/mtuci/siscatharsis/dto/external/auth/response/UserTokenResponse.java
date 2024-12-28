package ru.mtuci.siscatharsis.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenResponse {
    @NotBlank(message = "Login is empty")
    private String token;
}
