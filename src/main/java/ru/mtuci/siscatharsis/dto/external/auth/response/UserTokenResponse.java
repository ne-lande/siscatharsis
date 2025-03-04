package ru.mtuci.siscatharsis.dto.external.auth.response;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserTokenResponse {
    private String accessToken;

    @NotBlank(message = "Login is empty")
    private String token;
}
