package ru.mtuci.siscatharsis.dto.external.profile.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
        @NotBlank
        String password;
}
